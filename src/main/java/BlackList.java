import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class BlackList {

    public static void blackListUser(String uID, String reason, String staffID, JDA jda){
        try {
            FileWriter myWriter = new FileWriter(Storage.blackListFilePath, true);
            myWriter.write(uID + "\n" + reason + "\n" + staffID + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the blacklist file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the blacklist file.");
            e.printStackTrace();
        }
        blackListLog(uID, reason, staffID, jda);
    }

    public static void removeBlackList(String uID, String modID, JDA jda){
        ArrayList<String> blacklist = new ArrayList<>();

        try {
            File myObj = new File(Storage.blackListFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                blacklist.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file.");
            e.printStackTrace();
        }

        for(int i = 0; i < blacklist.size(); i++){
            if(blacklist.get(i).startsWith(uID)){
                blacklist.remove(i+2);
                blacklist.remove(i+1);
                blacklist.remove(i);
                break;
            }
        }

        try {
            FileWriter myWriter = new FileWriter(Storage.blackListFilePath);
            for (String s : blacklist) {
                myWriter.write(s + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the blacklist file after rewriting.");
        } catch (IOException e) {
            System.out.println("An error occurred while rewriting to the blacklist file.");
            e.printStackTrace();
        }

        removeBlackListLog(uID, modID, jda);
    }

    public static boolean isBlackList(String uID){
        Boolean x = false;

        ArrayList<String> blacklist = new ArrayList<>();

        try {
            File myObj = new File(Storage.blackListFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                blacklist.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file.");
            e.printStackTrace();
        }

        for(int i = 0; i < blacklist.size(); i += 3){
            if(blacklist.get(i).startsWith(uID)){
                x = true;
                break;
            }
        }

        return x;
    }

    public static void blackListLog(String uID, String reason, String staffID, JDA jda){
        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.archiveC);


        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("BlackList Log");
        eb.addField("Blacklisted user", uID, false);
        eb.addField("Reason", reason, false);
        eb.addField("Staff user", staffID, false);

        assert c != null;
        c.sendMessageEmbeds(eb.build()).queue();
    }

    public static void removeBlackListLog(String uID, String staffID, JDA jda){
        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.archiveC);


        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("BlackList Log");
        eb.addField("User removed from blacklist", uID, false);
        eb.addField("Staff", staffID, false);

        assert c != null;
        c.sendMessageEmbeds(eb.build()).queue();
    }
}