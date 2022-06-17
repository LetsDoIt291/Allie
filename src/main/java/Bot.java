import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
public class Bot {

    static EventWaiter waiter = new EventWaiter();

    static boolean botDebug = false;

    public static void main(String[] args) throws LoginException {
        Storage.clearOpenTicket();
        Storage.clearActiveAppeals();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId("395011406426144778");
        client.setPrefix("-");
        client.addCommands(new GameReportCommand(waiter), new AppealCommand(waiter));
        client.useHelpBuilder(false);
        client.setActivity(Activity.listening("-help"));

        JDABuilder jda = JDABuilder.createDefault("")
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new Commands(), waiter, client.build());

        jda.build();
    }
}