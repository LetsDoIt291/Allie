import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ReportTicket {

    public String userID;
    public String suspect;
    public String evidence;
    public String time;
    public String reason;

    public ReportTicket(String userID, String suspect, String evidence, String time, String reason){
        this.userID = userID;
        this.suspect = suspect;
        this.evidence = evidence;
        this.time = time;
        this.reason = reason;
    }

    public static void writeToFile(String userID, String suspect, String evidence, String time, String reason){
        String report = userID + "\n" + time + "\n" + suspect + "\n" + reason + "\n" + evidence;

        //writes the report into a given file. append is true so the previous content is not overridden
        try {
            FileWriter myWriter = new FileWriter(Storage.reportsFilePath, true);
            myWriter.write(report + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the reports file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the reports file.");
            e.printStackTrace();
        }
    }

    public static ArrayList<EmbedBuilder> searchSuspect(String suspect){

        ArrayList<ReportTicket> reports = Storage.reportSort();

        ArrayList<ReportTicket> selectedReports = new ArrayList<>();

        ArrayList<String> reportIDs = new ArrayList<>();

        ArrayList<EmbedBuilder> reportFormat = new ArrayList<>();

        //searches for all the reports from a specific person
        for(int i = 0; i < reports.size(); i++){
            if(reports.get(i).suspect.equalsIgnoreCase(suspect)){
                selectedReports.add(reports.get(i));
                reportIDs.add(String.valueOf(i));
            }
        }

        if(!selectedReports.isEmpty()){
            for(int i = 0; i < selectedReports.size(); i++){
                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("Game Report ID: " + i,"Reporter: " + reports.get(i).userID + "\nReason: " + reports.get(i).reason + "\nEvidence: " + reports.get(i).evidence, false);

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game Reports for user " + suspect);

                reportFormat.add(eb);
            }
        }

        return reportFormat;
    }

    public static ArrayList<EmbedBuilder> searchUser(String userID){
        boolean found = false;

        ArrayList<ReportTicket> reports = Storage.reportSort();

        ArrayList<ReportTicket> selectedReports = new ArrayList<>();

        ArrayList<String> reportIDs = new ArrayList<>();

        ArrayList<EmbedBuilder> reportFormat = new ArrayList<>();

        //searches for all the reports from a specific person
        for(int i = 0; i < reports.size(); i++){
            if(reports.get(i).userID.equals(userID)){
                found = true;
                selectedReports.add(reports.get(i));
                reportIDs.add(String.valueOf(i));
            }
        }

        if(!selectedReports.isEmpty()){


            for(int i = 0; i < selectedReports.size(); i++){
                //makes everything pretty
                EmbedBuilder eb = new EmbedBuilder();

                eb.addField("Game Report ID: " + reportIDs.get(i),"Suspect: " + selectedReports.get(i).suspect + "\nReason: " + selectedReports.get(i).reason + "\nEvidence: " + selectedReports.get(i).evidence, false);

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game Reports by " + userID);

                reportFormat.add(eb);
            }
        }

        return reportFormat;

    }

    public static void reportFormat(ReportTicket r, JDA jda){

        System.out.println("Report is being built - C: ReportTicket M: reportFormat");
        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.gameTicketC);


        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Game Report ID: " + (Storage.reportSort().size()-1));
        eb.addField("Suspect", r.suspect, false);
        eb.addField("Reason", r.reason, false);
        eb.addField("Evidence", r.evidence, false);
        eb.setFooter("Report submitted by: " + r.userID);

        assert c != null;
        c.sendMessageEmbeds(eb.build())
                .setActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.success("acceptReport," + (Storage.reportSort().size()-1), "Accept"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.danger("denyReport," + (Storage.reportSort().size()-1), "Deny"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customReportAccepted," + (Storage.reportSort().size()-1),"Accept (CM)"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customReportDenied," + (Storage.reportSort().size()-1),"Deny (CM)"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("userIncorrect," + (Storage.reportSort().size()-1),"Username Incorrect")
                ).queue();
    }

    public static void reportFormat(ReportTicket r, JDA jda, int rID){

        System.out.println("Report is being built - C: ReportTicket M: reportFormat");
        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.gameTicketC);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Game Report ID: " + rID);
        eb.addField("Suspect", r.suspect, false);
        eb.addField("Reason", r.reason, false);
        eb.addField("Evidence", r.evidence, false);
        eb.setFooter("Report submitted by: " + r.userID);

        assert c != null;
        c.sendMessageEmbeds(eb.build())
                .setActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.success("acceptReport," + rID, "Accept"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.danger("denyReport," + rID, "Deny"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customReportAccepted," + rID,"Accept (CM)"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("customReportDenied," + rID,"Deny (CM)"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("userIncorrect," + rID,"Username Incorrect")
                ).queue();
    }

    public String toString(){
        return suspect;
    }

    public static void archive(String rID, String modID, int type, JDA jda){
        ReportTicket report = Storage.reportSort().get(Integer.parseInt(rID));
        String status;

        if(type == 0)
            status = "accepted";
        else
            status = "denied";


        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.archiveC);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Game Report ID: " + rID);
        eb.addField("Suspect", report.suspect, false);
        eb.addField("Reason", report.reason, false);
        eb.addField("Evidence", report.evidence, false);
        eb.setFooter("Report submitted by: " + report.userID + "\nReport " + status + " by: " + modID);

        assert c != null;
        c.sendMessageEmbeds(eb.build()).queue();

    }
}
