import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AppealTicket{

    public String userID;
    public String robloxUsername;
    public String banReason;
    public String time;
    public String reasonToUnban;

    public AppealTicket(String userID, String robloxUsername, String banReason, String time, String reasonToUnban){
        this.userID = userID;
        this.robloxUsername = robloxUsername;
        this.banReason = banReason;
        this.time = String.valueOf(time);
        this.reasonToUnban = reasonToUnban;
    }

    public static ArrayList<EmbedBuilder> searchAppealer(String appealer) {

        ArrayList<AppealTicket> appeals = Storage.appealSort();

        ArrayList<AppealTicket> selectedAppeals = new ArrayList<>();

        ArrayList<String> appealIDs = new ArrayList<>();

        //searches for all the reports from a specific person
        for (int i = 0; i < appeals.size(); i++) {
            if (appeals.get(i).userID.equalsIgnoreCase(appealer)) {
                selectedAppeals.add(appeals.get(i));
                appealIDs.add(String.valueOf(i));
            }
        }

        ArrayList<EmbedBuilder> appealList = new ArrayList<>();

        if(!selectedAppeals.isEmpty()){
            for(int i = 0; i < selectedAppeals.size(); i++){
                //makes everything look pretty
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Appeal for user " + appealer);

                eb.addField("Appeal ID: " + i,"**Roblox user**: " + appeals.get(i).robloxUsername + "\n**Ban Reason**: " + appeals.get(i).banReason + "\n**Reason to be unbanned**: " + appeals.get(i).reasonToUnban, false);

                appealList.add(eb);
            }
        }

        return appealList;
    }

    public static void writeToFile(String userID, String robloxUsername, String banReason, String time, String reasonToUnban){
        String appeal = userID + "\n" + robloxUsername + "\n" + banReason + "\n" + time + "\n" + reasonToUnban;

        //writes the report into a given file. append is true so the previous content is not overridden2
        try {
            FileWriter myWriter = new FileWriter(Storage.appealsFilePath, true);
            myWriter.write(appeal + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the Appeals file. AppealTicket");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the Appeals file. AppealTicket");
            e.printStackTrace();
        }
    }

    public static void appealFormat(AppealTicket r, JDA jda){

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.gameTicketC);


        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Appeal ID: " + (Storage.appealSort().size()-1));
        eb.addField("Username", r.robloxUsername, false);
        eb.addField("Reason for ban", r.banReason, false);
        eb.addField("Reason to be unbanned", r.reasonToUnban, false);
        eb.setFooter("Appeal submitted by: " + r.userID);

        assert c != null;
        c.sendMessageEmbeds(eb.build())
                .setActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.success("acceptAppeal," + (Storage.appealSort().size()-1), "Accept"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.danger("denyAppeal," + (Storage.appealSort().size()-1), "Deny"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customAppealAccepted," + (Storage.appealSort().size()-1),"Accept (CM)"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customAppealDenied," + (Storage.appealSort().size()-1),"Deny (CM)"))
                .queue();
    }

    public static void appealFormat(AppealTicket r, JDA jda, int aID){

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.gameTicketC);


        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Appeal ID: " + aID);
        eb.addField("Username", r.robloxUsername, false);
        eb.addField("Reason for ban", r.banReason, false);
        eb.addField("Reason to be unbanned", r.reasonToUnban, false);
        eb.setFooter("Appeal submitted by: " + r.userID);

        assert c != null;
        c.sendMessageEmbeds(eb.build())
                .setActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.success("acceptAppeal," + aID, "Accept"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.danger("denyAppeal," + aID, "Deny"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customAppealAccepted," + aID,"Accept (CM)"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customAppealDenied," + aID,"Deny (CM)"))
                .queue();
    }

    public static void archive(String aID, String modID, int type, JDA jda){
        ArrayList<AppealTicket> appeals = Storage.appealSort();
        String status;

        if(type == 0)
            status = "accepted";
        else
            status = "denied";

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.archiveC);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Appeal ID: " + aID);
        eb.addField("Username", appeals.get(Integer.parseInt(aID)).robloxUsername, false);
        eb.addField("Reason for ban", appeals.get(Integer.parseInt(aID)).banReason, false);
        eb.addField("Reason to be unbanned", appeals.get(Integer.parseInt(aID)).reasonToUnban, false);
        eb.setFooter("Appeal submitted by: " + appeals.get(Integer.parseInt(aID)).userID + "\nAppeal " + status + " by: " + modID);

        assert c != null;
        c.sendMessageEmbeds(eb.build()).queue();

    }
}