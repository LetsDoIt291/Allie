import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Commands extends ListenerAdapter {

    public String prefix = "-";

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //checks is the message is from a bot

        if(event.getAuthor().isBot())
            return;

        //checks if the message starts with the prefix
        if(!event.getMessage().getContentRaw().startsWith(prefix))
            return;

        //grabs event info
        User author = event.getAuthor();
        Member member = event.getMember();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        //if bot is in debug mode, return unless it's me
        if(Bot.botDebug){
            if(!author.getId().equals("395011406426144778")){
                return;
            }
        }

        //splits the contents of the message into an array
        String[] args = message.getContentRaw().split(" ");

        //checks if the message is from a private channel
        if(event.isFromType(ChannelType.PRIVATE)){

            //displays DM commands
            if(args[0].equalsIgnoreCase(prefix + "help")){
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Commands");
                eb.addField("-gamereport", "Starts an in-game report and gives relevant information", false);
                eb.addField("-gamereportinfo", "Gives information on in-game reports", false);
                eb.addField("-appeal", "Starts an in-game appeal and gives relevant information", false);

                channel.sendMessageEmbeds(eb.build()).queue();
                return;
            }

            //displays game report information
            if(args[0].equalsIgnoreCase(prefix + "gamereportinfo")){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Game Report Information");
                eb.addField("Exploit Reports", "> Must be a video. The only exception are images that display the user flying (or any other impossible positions for someone to be in) and also displays their username."
                        + "\n\n> Videos must be uploaded to sites like https://www.youtube.com/ or any other video streaming platform (please avoid using streamable as the videos do expire)."
                        + "\n\n> Images must be uploaded to https://imgur.com/"
                        + "\n\n> The video/image must show:\n> 1. Exploits (the Kill feed or in-game stats are not valid)\n> 2. A username that is readable (a display name is not a username).\n ",false);
                eb.addField("Toxicity Reports", "> Must be a video or screenshot that displays both chat and the scoreboard. (This is to avoid impersonation because of display names)"
                        + "\n\n> We do not punish for general toxicity like \"gg ez,\" \"you're trash,\" etc.",false);

                channel.sendMessageEmbeds(eb.build()).queue();
                return;
            }

            return;
        }

        //if the user does not have ban perms, ignore message
        assert member != null;
        if(!(member.hasPermission(Permission.BAN_MEMBERS))){
            event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> No.").queue(Message -> {Message.delete().queueAfter(5, TimeUnit.SECONDS); message.delete().queueAfter(5, TimeUnit.SECONDS);});
            return;
        }

        //search appeals made by a discord user
        if(args[0].equalsIgnoreCase(prefix + "searchappealer")){
            ArrayList<EmbedBuilder> appeals = AppealTicket.searchAppealer(args[1]);

            if(!appeals.isEmpty()){
                for(int i = 0; i < appeals.size(); i++){
                    channel.sendMessageEmbeds(appeals.get(i).build()).queue();
                }
            }else{
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game appeals by " + args[1]);
                eb.setDescription("No appeals found");

                channel.sendMessageEmbeds(eb.build()).queue();
            }

            return;
        }

        //sets the staff member available for mod call
        if(args[0].equalsIgnoreCase(prefix + "available") || args[0].equalsIgnoreCase(prefix + "active")){
            if(ModCall.checkIfActive(author.getId())){
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Error");
                eb.setDescription("You are already available for mod calls");

                channel.sendMessageEmbeds(eb.build()).queue(Message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }

            ModCall.addMod(Storage.modCallMessage, author.getId(), event.getJDA());

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setDescription("You are now available for mod calls.");

            channel.sendMessageEmbeds(eb.build()).queue();
        }

        //sets the staff member unavailable for mod call
        if(args[0].equalsIgnoreCase(prefix + "unavailable" ) || args[0].equalsIgnoreCase(prefix + "inactive")){
            if(!ModCall.checkIfActive(author.getId())){
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Error");
                eb.setDescription("You aren't available for mod calls");

                channel.sendMessageEmbeds(eb.build()).queue();
                return;
            }

            ModCall.removeMod(Storage.modCallMessage, author.getId(), event.getJDA());

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setDescription("You are no longer available for mod calls.");

            channel.sendMessageEmbeds(eb.build()).queue();
        }

        //search reports made by discord user
        if(args[0].equalsIgnoreCase(prefix + "searchUser")){
            ArrayList<EmbedBuilder> reports = ReportTicket.searchUser(args[1]);

            if(!reports.isEmpty()){
                for(int i = 0; i < reports.size(); i++){
                    channel.sendMessageEmbeds(reports.get(i).build()).queue();
                }
            }else{
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game Reports by " + args[1]);
                eb.setDescription("No reports found");

                channel.sendMessageEmbeds(eb.build()).queue();
            }

            return;
        }

        //search reports made with suspects roblox username
        if(args[0].equalsIgnoreCase(prefix + "searchSuspect")){

            ArrayList<EmbedBuilder> reports = ReportTicket.searchSuspect(args[1]);

            if(!reports.isEmpty()){
                for(int i = 0; i < reports.size(); i++){
                    channel.sendMessageEmbeds(reports.get(i).build()).queue();
                }
            }else{
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game Reports for " + args[1]);
                eb.setDescription("No reports found");

                channel.sendMessageEmbeds(eb.build()).queue();
            }

            return;
        }

        //whitelists the user for the ticket system (no limit on reports)
        if(args[0].equalsIgnoreCase(prefix + "whitelist")){



        //blacklists the user for the ticket system (restricted from making any tickets)
        if(args[0].equalsIgnoreCase(prefix + "blacklist")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(102, 214, 238));

            //makes sure there is at least 3 arguments given (command, ID, reason)
            if(!(args.length < 3)) {
                //checks if the ID exists
                if(!(event.getJDA().getUserById(args[1]) == null)){
                    //checks if they're already blacklisted
                    if(!(BlackList.isBlackList(args[1]))){
                        BlackList.blackListUser(args[1], args[2], author.getId(), event.getJDA());
                        eb.setDescription("The user \"" + args[1] + "\" has been blacklisted");

                        channel.sendMessageEmbeds(eb.build()).queue();
                    }else {
                        eb.setTitle("Error");
                        eb.setDescription("The user \"" + args[1] + "\" has already been blacklisted");
                        channel.sendMessageEmbeds(eb.build()).queue();
                    }
                }else{
                    eb.setTitle("Error");
                    eb.setDescription("User does not exist");
                    channel.sendMessageEmbeds(eb.build()).queue();
                }

                //checks if there is a given reason
            }else if (args.length == 2){
                eb.setTitle("Error");
                eb.setDescription("Please enter a reason for the blacklist");
                channel.sendMessageEmbeds(eb.build()).queue();
            }else{
                eb.setTitle("Error");
                eb.setDescription("Please enter a discord ID and reason for the blacklist");
                channel.sendMessageEmbeds(eb.build()).queue();
            }
            return;
        }

        //checks if the user is currently blacklisted and removes the blacklist if they are
        if(args[0].equalsIgnoreCase(prefix + "removeblacklist") || args[0].equalsIgnoreCase(prefix + "blacklistremove")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(102, 214, 238));

            if(args.length < 2) return;

            if(BlackList.isBlackList(args[1])){
                BlackList.removeBlackList(args[1], author.getId(), event.getJDA());

                eb.setDescription("The user \"" + args[1] + "\" has been removed from the blacklist");

                channel.sendMessageEmbeds(eb.build()).queue();
            }else{
                eb.setTitle("Error");
                eb.setDescription("The user \"" + args[1] + "\" is not blacklisted");

                channel.sendMessageEmbeds(eb.build()).queue();
            }
        }

        //server help command
        if(args[0].equalsIgnoreCase(prefix + "help") || args[0].equalsIgnoreCase(prefix + "commands") || args[0].equalsIgnoreCase(prefix + "cmds")){
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setAuthor("Server Commands");
            eb.addField("-searchuser [discord ID]", "Returns a list of reports made by the discord user", false);
            eb.addField("-searchsuspect [roblox username]","Returns a list of reports for the roblox user",false);
            eb.addField("-searchappealer [discord ID]","Returns a list of appeals for the discord user",false);
            eb.addField("-blacklist [discord ID] [reason]", "Blacklists a discord user from making tickets", false);
            eb.addField("-removeblacklist [discord ID]", "Removes the blacklist from a discord user", false);
            eb.addField("-active", "Sets you as available for mod calls", false);
            eb.addField("-inactive", "Sets you as unavailable for mod calls", false);

            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        //server admin commands
        if(args[0].equalsIgnoreCase(prefix + "admincommands") || args[0].equalsIgnoreCase(prefix + "acommands")){
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setAuthor("Admin Commands");
            eb.addField("-buildmodcall", "Builds the message in the mod call channel.\nRequires permission to use.", false);
            eb.addField("-clearmodcall", "Clears all users from mod call.\nRequires permission to use.", false);
            eb.addField("-debug", "Stops all interactions.\nRequires permission to use.", false);
            eb.addField("-buildReportM", "Creates the message directing users to Allie's DMs for reports.\nRequires permission to use.", false);
            eb.addField("-buildAppealM", "Creates the message directing users to Allie's DMs for appeals.\nRequires permission to use.", false);
            eb.addField("-gameTicketC", "Sets the game ticket channel.\nRequires permission to use.", false);
            eb.addField("-ticketArchiveC", "Sets the archive channel.\nRequires permission to use.", false);
            eb.addField("-server", "Sets the server.\nRequires permission to use.", false);
            eb.addField("-modCallC", "Sets the mod call channel.\nRequires permission to use.", false);
            eb.addField("-discordTicketC", "Sets the discord ticket channel.\nRequires permission to use.", false);



            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String ownerID = "395011406426144778";

        //builds the embedded message for mod call - requires admin perms or be the bot owner.
        if(args[0].equalsIgnoreCase(prefix + "buildmodcall") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            ModCall.buildChannel(author.getJDA());
            return;
        }

        //clears users in mod call - requires admin perms or be the bot owner
        if(args[0].equalsIgnoreCase(prefix + "clearmodcall") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            ModCall.clear(author.getJDA());
            return;
        }

        //puts the bot in debug mode which ignores all messages/commands except those from the owner
        if(args[0].equalsIgnoreCase(prefix + "debug") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            if(Bot.botDebug){
                Bot.botDebug = false;
                channel.sendMessage("Bot is no longer in debug mode").queue();
            }else{
                Bot.botDebug = true;
                channel.sendMessage("Bot is in debug mode").queue();
            }
        }

        //creates the message directing users to DM Allie for reports
        if(args[0].equalsIgnoreCase(prefix + "buildReportM") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(102, 214, 238));
            eb.setAuthor("In-Game Player Reports");
            eb.setDescription("To report a player DM me \"-gamereport\"");
            eb.setFooter(">>In order to make a report you need to allow direct messages<<");

            channel.sendMessageEmbeds(eb.build()).queue();
            message.delete().queue();
        }

        //creates the message directing users to DM Allie for appeals
        if(args[0].equalsIgnoreCase(prefix + "buildAppealM") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(102, 214, 238));
            eb.setAuthor("In-Game unban appeals");
            eb.setDescription("To make an appeal DM me \"-appeal\"");
            eb.setFooter(">>In order to make an appeal you need to allow direct messages<<");

            channel.sendMessageEmbeds(eb.build()).queue();
            message.delete().queue();

        }

        //changes the game ticket channel
        if(args[0].equalsIgnoreCase(prefix + "gameTicketC") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            Storage.changeInfoFile(args[1], 0);
        }

        //changes the ticket archive channel
        if(args[0].equalsIgnoreCase(prefix + "ticketArchiveC") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            Storage.changeInfoFile(args[1], 3);
        }

        //changes the server
        if(args[0].equalsIgnoreCase(prefix + "server") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            Storage.changeInfoFile(args[1], 2);
        }

        //changes the mod call channel
        if(args[0].equalsIgnoreCase(prefix + "modCallC") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            Storage.changeInfoFile(args[1], 4);
        }

        //changes the discord ticket channel
        if(args[0].equalsIgnoreCase(prefix + "discordTicketC") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            Storage.changeInfoFile(args[1], 6);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();

        //returns if not from guild
        if(!event.isFromGuild()){
            return;
        }

        //if bot is in debug mode, return unless it's me
        if(Bot.botDebug){
            if(!event.getUser().getId().equals("395011406426144778")){
                return;
            }
        }

        //checks if the user has ban permission (I.E. is a mod or higher) and returns if they don't
        assert member != null;
        if(!(member.hasPermission(Permission.BAN_MEMBERS))){
            System.out.println("No button perms");
            event.deferEdit().queue();
            return;
        }

        //if the button click is in the mod call channel
        if(event.getChannel().getId().equals(Storage.getModCallC())){
            //if the available button was clicked
            if(event.getComponentId().startsWith("available")){
                event.deferEdit().queue();
                //checks if the staff member is already active
                if(!ModCall.checkIfActive(event.getUser().getId()))
                    //adds the staff member to mod call
                    ModCall.addMod(event.getMessageId(), event.getUser().getId(), event.getJDA());
            }

            //if the unavailable button was clicked
            if(event.getComponentId().startsWith("unavailable")){
                event.deferEdit().queue();
                //checks if the staff member is inactive already
                if(ModCall.checkIfActive(event.getUser().getId()))
                    //removes the staff member from mod call
                    ModCall.removeMod(event.getMessageId(), event.getUser().getId(), event.getJDA());

            }
        }

        //if the button click is in the game ticket channel
        if (event.getChannel().getId().equals(Storage.getGameTicketC())) {

            //if the accept report button is clicked
            if (event.getComponentId().startsWith("acceptReport")) {
                event.deferEdit().queue();

                //deletes the report message
                event.getMessage().delete().queue();

                //storing info about the report
                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                //archives the report to the appropriate channel
                ReportTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Game report for the user \"" +suspect + "\" has been accepted!");
                eb.setDescription("Thank you for reporting!");
                eb.setFooter(">> Replying to this message will do nothing <<");

                //messages the reporter with the above message
                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
                return;
            }

            //if the deny report button is clicked
            if (event.getComponentId().startsWith("denyReport")) {
                event.deferEdit().queue();

                //deletes the report message
                event.getMessage().delete().queue();

                //storing info about the report
                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                //archives the report to the appropriate channel
                ReportTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Game report for the user \"" +suspect + "\" has been denied.");
                eb.setDescription("> We either concluded the user in question was not exploiting or the evidence provided was insufficient. Please review what we need in a report by using the command -gamereportinfo");
                eb.setFooter(">> Replying to this message will do nothing <<");

                //messages the reporter with the above message
                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
            }

            //if the accepted report (CM) is clicked
            if (event.getComponentId().startsWith("customReportAccepted")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> {Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Game report for the user \"" +suspect + "\" has been accepted!");
                            eb.setDescription("You've received the following message from a mod about your report: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            ReportTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();

                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded").queue(Message3 -> {Message3.delete().queueAfter(5, TimeUnit.SECONDS);});
                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 3 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queue(); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 3 minutes, time out
                        3, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.SECONDS); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
            });}

            //if the username incorrect button is clicked
            if (event.getComponentId().startsWith("userIncorrect")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");

                System.out.println("Report ID: " + args[1] + " - userIncorrect");

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the correct username. <@" + event.getUser().getId() + ">").queue(Message -> {Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            Message.delete().queue();
                            event.getChannel().sendMessage("Is this the correct username? \"" + e1.getMessage().getContentRaw() + "\"\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));

                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Username has been changed").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));

                                            Storage.reportChangeUsername(args[1], e1.getMessage().getContentRaw());


                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }else{
                                            event.getChannel().sendMessage("Ticket has been returned unchanged").queue(Message3 -> {Message.delete().queueAfter(5, TimeUnit.SECONDS); Message3.delete().queueAfter(5, TimeUnit.SECONDS);});
                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 3 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queue(); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 3 minutes, time out
                        3, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.SECONDS); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                });}

            //if the denied report (CM) is clicked
            if (event.getComponentId().startsWith("customReportDenied")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;


                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> {Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Game report for the user \"" +suspect + "\" has been denied.");
                            eb.setDescription("You've received the following message from a mod about your report: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            ReportTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();
                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded").queue(Message3 -> Message.delete().queueAfter(5, TimeUnit.SECONDS));
                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 1 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {System.out.println("TRUE"); event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 3 minutes, time out
                        3, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(),  Integer.parseInt(args[1]));});
            });}

            //if the accept appeal button is clicked
            if (event.getComponentId().startsWith("acceptAppeal")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();


                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;


                AppealTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Your game appeal has been accepted!");
                eb.setFooter(">> Replying to this message will do nothing <<");

                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
                return;
            }

            //if the deny appeal button is clicked
            if (event.getComponentId().startsWith("denyAppeal")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;

                AppealTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Your game appeal has been denied");
                eb.setDescription("You may make another appeal.");
                eb.setFooter(">> Replying to this message will do nothing <<");

                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
            }

            //if the accepted appeal (CM) is clicked
            if (event.getComponentId().startsWith("customAppealAccepted")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;

                AppealTicket appeal = Storage.appealSort().get(Integer.parseInt(args[1]));

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Your game appeal has been accepted!");
                            eb.setDescription("You've received the following message from a mod about your appeal: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            AppealTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();
                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded.\nTicket returned.").queue(Message3 -> Message.delete().queueAfter(15, TimeUnit.SECONDS));
                                            AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 1 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                            Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 2 minutes, time out
                        2, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));}));
            }

            //if the denied appeal (CM) is clicked
            if (event.getComponentId().startsWith("customAppealDenied")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;

                AppealTicket appeal = Storage.appealSort().get(Integer.parseInt(args[1]));

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Your game appeal has been denied.");
                            eb.setDescription("You've received the following message from a mod about your appeal: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            //event.getMessage().delete().queue();
                                            AppealTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();
                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded.\nTicket returned.").queue(Message3 -> Message.delete().queueAfter(15, TimeUnit.SECONDS));
                                            AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 1 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                            Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 2 minutes, time out
                        2, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));}));
            }
        }
    }
}