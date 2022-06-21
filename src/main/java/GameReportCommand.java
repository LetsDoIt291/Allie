import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class GameReportCommand extends Command {

    private final EventWaiter waiter; // This variable is used to define the waiter, and call it from anywhere in this class.

    public String suspect;
    public String reason;
    public String evidence;

    public GameReportCommand(EventWaiter waiter) {
        this.waiter = waiter; // Define the waiter
        this.name = "gamereport"; // The command
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event){
        if(!event.isFromType(ChannelType.PRIVATE)) return;

        //if bot is in debug mode, return unless it's me
        if(Bot.botDebug){
            if(!event.isOwner()){
                return;
            }
        }

        if(Storage.checkUserOpenTicket(event.getAuthor().getId()) || Storage.checkUserActiveAppeal(event.getAuthor().getId())){
            event.replyInDm("You already have a ticket open.");
            return;
        }

        //checks is the user has made 5 reports in the last hour
        if(SpamPrevention.checkTimeReports(event.getAuthor().getId())){
            SpamPrevention x = SpamPrevention.spamPreventionReport(event.getAuthor().getId());

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setAuthor("Report ticket error");
            eb.setDescription("You have made 5 tickets in the last hour. You may make another ticket in " + x.minutes + " minutes & " + x.seconds + " seconds.");

            event.getPrivateChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        if(BlackList.isBlackList(event.getAuthor().getId())){
            event.replyInDm("You are blacklisted from making tickets");
            return;
    }

        Storage.addUserToOpenTicket(event.getAuthor().getId());

        event.getPrivateChannel().sendMessageEmbeds(gameReportInfo().build()).setActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("gameReportYes","I understand"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("gameReportNo","I don't understand"))
                .queue(Message -> { waiter.waitForEvent(ButtonInteractionEvent.class,
                        // make sure it's by the same channel
                        e -> e.getChannel().equals(event.getChannel()),
                        // response
                        e5 -> {
                            if(e5.getComponentId().equals("gameReportYes")){
                                System.out.println("yes true");
                                Message.delete().queue();

                                event.replyInDm(askForUsername().build()); // Respond to the command with a message.

                                // wait for a response
                                waiter.waitForEvent(MessageReceivedEvent.class,
                                        // make sure it's by the same user, and in the same channel, and for safety, a different message
                                        e -> e.getAuthor().equals(event.getAuthor())
                                                && e.getChannel().equals(event.getChannel())
                                                && !e.getMessage().getContentRaw().equals(event.getMessage().getContentRaw())
                                                && e.getMessage().getAttachments().isEmpty()
                                                && e.getMessage().getContentRaw().length() >= 3
                                                && e.getMessage().getContentRaw().length() <= 20,
                                        // response
                                        e1 -> {
                                            event.replyInDm(askForReason().build());
                                            suspect = e1.getMessage().getContentRaw().replace("\n", " ");
                                            waiter.waitForEvent(MessageReceivedEvent.class,
                                                    // make sure it's by the same user, and in the same channel, and for safety, a different message
                                                    e -> e.getAuthor().equals(event.getAuthor())
                                                            && e.getChannel().equals(event.getChannel())
                                                            && !e.getMessage().getContentRaw().equals(e1.getMessage().getContentRaw())
                                                            && !e.getMessage().getContentRaw().equals(event.getMessage().getContentRaw())
                                                            && e.getMessage().getAttachments().isEmpty()
                                                            && e.getMessage().getContentRaw().length() < 500,
                                                    // response
                                                    e2 -> {
                                                        event.replyInDm(askForEvidence().build());
                                                        reason = e2.getMessage().getContentRaw().replace("\n", " ");
                                                        waiter.waitForEvent(MessageReceivedEvent.class,
                                                                // make sure it's by the same user, and in the same channel, and for safety, a different message
                                                                e -> e.getAuthor().equals(event.getAuthor())
                                                                        && e.getChannel().equals(event.getChannel())
                                                                        && !e.getMessage().getContentRaw().equals(e1.getMessage().getContentRaw())
                                                                        && !e.getMessage().getContentRaw().equals(e2.getMessage().getContentRaw())
                                                                        && !e.getMessage().getContentRaw().equals(event.getMessage().getContentRaw())
                                                                        && e.getMessage().getAttachments().isEmpty()
                                                                        && e.getMessage().getContentRaw().length() < 250,
                                                                // response
                                                                e3 -> {
                                                                    evidence = e3.getMessage().getContentRaw().replace("\n", " ");
                                                                    event.replyInDm(reportDone().build());
                                                                    Storage.removeUserFromOpenTicket(event.getAuthor().getId());
                                                                    ReportTicket.writeToFile(event.getAuthor().getId(), suspect, evidence, String.valueOf(System.currentTimeMillis()), reason);
                                                                    ReportTicket.reportFormat(new ReportTicket(event.getAuthor().getId(), suspect, evidence, String.valueOf(System.currentTimeMillis()), reason), event.getJDA());

                                                                },
                                                                // if the user takes more than 3 minutes, time out and remove user from openTicket storage
                                                                1, TimeUnit.MINUTES, () -> {event.replyInDm("Sorry, you took too long. Feel free to try again."); Storage.removeUserFromOpenTicket(event.getAuthor().getId());});

                                                    },
                                                    // if the user takes more than 3 minutes, time out and remove user from openTicket storage
                                                    1, TimeUnit.MINUTES, () -> {event.replyInDm("Sorry, you took too long. Feel free to try again."); Storage.removeUserFromOpenTicket(event.getAuthor().getId());});
                                        },
                                        // if the user takes more than 3 minutes, time out and remove user from openTicket storage
                                        1, TimeUnit.MINUTES, () -> {event.replyInDm("Sorry, you took too long. Feel free to try again."); Storage.removeUserFromOpenTicket(event.getAuthor().getId());});
                            }else{
                                Storage.removeUserFromOpenTicket(event.getAuthor().getId());
                                Message.delete().queue();
                                event.replyInDm(askModForHelp().build());
                            }

                        },
                        // if the user takes more than 2 minutes
                        2, TimeUnit.MINUTES, () -> {event.replyInDm("Sorry, you took too long. Feel free to try again."); Message.delete().queue(); Storage.removeUserFromOpenTicket(event.getAuthor().getId());});});
    }

    public EmbedBuilder askModForHelp(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("If there's something you need explained or help with in regards to your game report, please contact a staff member.");

        return eb;
    }

    public EmbedBuilder askForUsername(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("What is the username of the user you are reporting?");
        eb.setDescription("> Make sure you type the username correctly.\n\n> Must be 3-20 characters");

        return eb;
    }

    public EmbedBuilder askForReason(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("What is the reason you are reporting this user?");
        eb.setDescription("> Please elaborate on what this user has done.\n\n> 500 character limit");

        return eb;
    }

    public EmbedBuilder askForEvidence(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("What evidence do you have of this occurring?");
        eb.setDescription("> If you want to post images please use https://imgur.com\n\n> If you want to post a video please use https://www.youtube.com or any other reliable site.\n\n> 250 character limit");
        eb.setFooter(">> We do not accept any type of files <<");

        return eb;
    }

    public EmbedBuilder reportDone(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("Thank you for reporting!");
        eb.setDescription("> You will receive a message when staff have reviewed your ticket.");

        return eb;
    }

    public EmbedBuilder gameReportInfo(){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("Game Report Information");
        eb.addField("Exploit Reports", "> Must be a video. The only exception are images that display the user flying (or any other impossible positions for someone to be in) and also displays their username."
                + "\n\n> Videos must be uploaded to sites like https://www.youtube.com/ or any other video streaming platform (please avoid using streamable as the videos do expire)."
                + "\n\n> Images must be uploaded to https://imgur.com/"
                + "\n\n> The video/image must show:\n> 1. Exploits (the Kill feed or in-game stats are not valid)\n> 2. A username that is readable (a display name is not a username).\n ",false);
        eb.addField("Toxicity Reports", "> Must be a video or screenshot that displays both chat and the scoreboard. (This is to avoid impersonation because of display names)"
                + "\n\n> We do not punish for general toxicity like \"gg ez,\" \"you're trash,\" etc.",false);

        return eb;
    }
}