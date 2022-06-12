import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class AppealCommand extends Command {

    private final EventWaiter waiter; // This variable is used to define the waiter, and call it from anywhere in this class.

    public String username;
    public String reasonForBan;
    public String reasonForUnban;

    public AppealCommand(EventWaiter waiter) {
        this.waiter = waiter; // Define the waiter
        this.name = "appeal"; // The command
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.isFromType(ChannelType.PRIVATE)) return;

        //if bot is in debug mode, return unless it's me
        if(Bot.botDebug){
            if(!event.isOwner()){
                return;
            }
        }

        if (Storage.checkUserOpenTicket(event.getAuthor().getId()) || Storage.checkUserActiveAppeal(event.getAuthor().getId())) {
            event.replyInDm("You already have a ticket open.");
            return;
        }

        //checks is the user has made 5 reports in the last hour
        if (SpamPrevention.checkAppealTime(event.getAuthor().getId())) {
            SpamPrevention x = SpamPrevention.spamPreventionAppeal(event.getAuthor().getId());

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setAuthor("Appeal ticket error");
            eb.setDescription("You've already made an appeal in the last 12 hours. You may make another appeal in **" + x.hours + " hours, " + x.minutes + " minutes & " + x.seconds + " seconds**.");

            event.getPrivateChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        if (BlackList.isBlackList(event.getAuthor().getId())) {
            event.replyInDm("You are blacklisted from making tickets");
            return;
        }

        Storage.addUserToActiveAppeals(event.getAuthor().getId());
        event.getPrivateChannel().sendMessageEmbeds(gameAppealInfo().build()).setActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("gameAppealYes", "I understand"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("gameAppealNo", "I don't understand"))
                .queue(Message -> {
                    waiter.waitForEvent(ButtonInteractionEvent.class,
                            // make sure it's by the same channel
                            e -> e.getChannel().equals(event.getChannel()),
                            // response
                            e5 -> {
                                if (e5.getComponentId().equals("gameAppealYes")) {
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
                                                event.replyInDm(askWhy().build());
                                                username = e1.getMessage().getContentRaw().replace("\n", " ");
                                                waiter.waitForEvent(MessageReceivedEvent.class,
                                                        // make sure it's by the same user, and in the same channel, and for safety, a different message
                                                        e -> e.getAuthor().equals(event.getAuthor())
                                                                && e.getChannel().equals(event.getChannel())
                                                                && !e.getMessage().getContentRaw().equals(e1.getMessage().getContentRaw())
                                                                && !e.getMessage().getContentRaw().equals(event.getMessage().getContentRaw())
                                                                && e.getMessage().getAttachments().isEmpty()
                                                                && e.getMessage().getContentRaw().length() < 150,
                                                        // response
                                                        e2 -> {
                                                            event.replyInDm(askForReason().build());
                                                            reasonForBan = e2.getMessage().getContentRaw().replace("\n", " ");
                                                            waiter.waitForEvent(MessageReceivedEvent.class,
                                                                    // make sure it's by the same user, and in the same channel, and for safety, a different message
                                                                    e -> e.getAuthor().equals(event.getAuthor())
                                                                            && e.getChannel().equals(event.getChannel())
                                                                            && !e.getMessage().getContentRaw().equals(e1.getMessage().getContentRaw())
                                                                            && !e.getMessage().getContentRaw().equals(e2.getMessage().getContentRaw())
                                                                            && !e.getMessage().getContentRaw().equals(event.getMessage().getContentRaw())
                                                                            && e.getMessage().getAttachments().isEmpty()
                                                                            && e.getMessage().getContentRaw().length() < 750,
                                                                    // response
                                                                    e3 -> {
                                                                        reasonForUnban = e3.getMessage().getContentRaw().replace("\n", " ");
                                                                        Storage.removeUserFromActiveAppeals(event.getAuthor().getId());
                                                                        AppealTicket.writeToFile(event.getAuthor().getId(), username, reasonForBan, String.valueOf(System.currentTimeMillis()), reasonForUnban);
                                                                        AppealTicket.appealFormat(new AppealTicket(event.getAuthor().getId(), username, reasonForBan, String.valueOf(System.currentTimeMillis()), reasonForUnban), event.getJDA());
                                                                        event.replyInDm(appealSent().build());
                                                                    },
                                                                    // if the user takes more than 3 minutes, time out and remove user from openTicket storage
                                                                    8, TimeUnit.MINUTES, () -> {
                                                                        event.replyInDm("Sorry, you took too long. Feel free to try again.");
                                                                        Storage.removeUserFromActiveAppeals(event.getAuthor().getId());
                                                                    });

                                                        },
                                                        // if the user takes more than 3 minutes, time out and remove user from openTicket storage
                                                        3, TimeUnit.MINUTES, () -> {
                                                            event.replyInDm("Sorry, you took too long. Feel free to try again.");
                                                            Storage.removeUserFromActiveAppeals(event.getAuthor().getId());
                                                        });
                                            },
                                            // if the user takes more than 3 minutes, time out and remove user from openTicket storage
                                            1, TimeUnit.MINUTES, () -> {
                                                event.replyInDm("Sorry, you took too long. Feel free to try again.");
                                                Storage.removeUserFromActiveAppeals(event.getAuthor().getId());
                                            });

                                } else {
                                    Storage.removeUserFromActiveAppeals(event.getAuthor().getId());
                                    Message.delete().queue();
                                    event.replyInDm(askModForHelp().build());
                                }

                            },
                            // if the user takes more than 2 minutes
                            2, TimeUnit.MINUTES, () -> {
                                event.replyInDm("Sorry, you took too long. Feel free to try again.");
                                Message.delete().queue();
                                Storage.removeUserFromActiveAppeals(event.getAuthor().getId());
                            });
                });
    }

    public EmbedBuilder askModForHelp(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("If there's something you need explained or help with in regards to your appeal, please contact a staff member.");

        return eb;
    }

    public EmbedBuilder askForUsername(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("What is your roblox username?");
        eb.setDescription(">> Make sure you type the username correctly <<");

        return eb;
    }

    public EmbedBuilder askWhy(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("Why were you banned?");
        eb.setDescription("> 150 character limit");

        return eb;
    }

    public EmbedBuilder askForReason(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("Why should you be unbanned");
        eb.setDescription("> 750 character limit");
        eb.setFooter(">> You have 8 minutes to complete this section <<");

        return eb;
    }

    public EmbedBuilder appealSent(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("Your appeal has been submitted");

        return eb;
    }

    public EmbedBuilder gameAppealInfo(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("Game Appeal Information");
        eb.setDescription("> This is an in-game appeal, not a discord appeal. " +
                "\n\n> You will be notified if your appeal was accepted or denied." +
                "\n\n> If your appeal was denied you may make another within 12 hours." +
                "\n\n> Honesty goes a long way.");

        return eb;
    }
}