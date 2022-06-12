import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class ModCall {
    //bot has to be restarted when modcall is built. commands wont work otherwise idk why. shit bot

    public static void buildChannel(JDA jda){
        Storage.clearModCall();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("List of available Staff\n");
        eb.setDescription("No Staff are available at this time.");
        eb.setFooter("> Buttons are only usable by staff");

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.modCallC);

        assert c != null;
        c.sendMessageEmbeds(eb.build())
                .setActionRow(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.success("available", "Available"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.danger("unavailable", "Unavailable"))
                .queue((Message) -> {
                    Storage.changeInfoFile(Message.getId(), 5);
                });

    }

    public static void addMod(String messageID, String modID, JDA jda){
        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.modCallC);

        Storage.addModToFile(modID);

        ArrayList<String> modList = Storage.modSort();
        String x = "";

        for(int i = 0; i < modList.size(); i++){
            x = x + "<@" + modList.get(i) + ">" + "\n";
        }


        System.out.println(x);
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("List of available Staff\n");
        eb.setDescription(x);
        eb.setFooter("> Ping an active staff member in general\n> Buttons are only usable by staff");


        assert c != null;
        c.editMessageEmbedsById(messageID, eb.build()).queue();
    }

    public static void removeMod(String messageID, String modID, JDA jda){
        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.modCallC);

        Storage.removeModFromFile(modID);

        ArrayList<String> modList = Storage.modSort();
        String x = "";

        if(modList.isEmpty()){
            x = "There are no Staff available";
        }else{
            for(int i = 0; i < modList.size(); i++){
                x = x + "<@" + modList.get(i) + ">" + "\n";
            }
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("List of available Staff\n");
        eb.setDescription(x);
        eb.setFooter("> Buttons are only usable by Staff");
        if(!modList.isEmpty())
            eb.setFooter("> Ping an active staff member in general\n> Buttons are only usable by staff");

        assert c != null;
        c.editMessageEmbedsById(messageID, eb.build()).queue();
    }

    public static Boolean checkIfActive(String modID){
        ArrayList<String> mods = Storage.modSort();
        boolean x = false;

        for(int i = 0; i < mods.size(); i++){
            if(mods.get(i).equals(modID)) {
                x = true;
                break;
            }
        }

        return x;
    }

    public static void clear(JDA jda){
        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.modCallC);

        Storage.clearModCall();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(102, 214, 238));
        eb.setTitle("List of available Staff\n");
        eb.setDescription("There are no Staff available");

        assert c != null;
        c.editMessageEmbedsById(Storage.modCallMessage, eb.build()).complete();
    }
}