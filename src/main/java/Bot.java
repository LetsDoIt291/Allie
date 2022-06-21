import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Bot {

    static EventWaiter waiter = new EventWaiter();

    static boolean botDebug = false;

    public static void main(String[] args) throws LoginException, FileNotFoundException {
        Storage.clearOpenTicket();
        Storage.clearActiveAppeals();

        //So I stop exposing my token and don't have to keep resetting it
        File myObj = new File("C:\\Users\\Letsd\\Desktop\\Token.txt");
        Scanner myReader = new Scanner(myObj);

        String token = myReader.nextLine();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId("395011406426144778");
        client.setPrefix("-");
        client.addCommands(new GameReportCommand(waiter), new AppealCommand(waiter));
        client.useHelpBuilder(false);
        client.setActivity(Activity.listening("-help"));

        JDABuilder jda = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new Commands(), waiter, client.build());

        jda.build();
    }
}