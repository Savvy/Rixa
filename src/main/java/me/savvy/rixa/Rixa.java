package me.savvy.rixa;

import me.savvy.rixa.commands.admin.BatchMoveCommand;
import me.savvy.rixa.commands.general.HelpCommand;
import me.savvy.rixa.commands.general.InfoCommand;
import me.savvy.rixa.commands.general.PingCommand;
import me.savvy.rixa.commands.general.ServerInfoCommand;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.mod.DeleteMessagesCommand;
import me.savvy.rixa.commands.mod.PurgeCommand;
import me.savvy.rixa.events.BotEvent;
import me.savvy.rixa.events.MessageEvent;
import me.savvy.rixa.modules.reactions.handlers.ReactionManager;
import me.savvy.rixa.modules.reactions.react.HelpReaction;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Timber on 5/7/2017.
 */
public class Rixa {

    private static List<JDA> shardsList;
    private static long timeUp;
    private static Rixa instance; // String search = event.getMessage().getContent().substring(event.getMessage().getContent().indexOf(" ") + 1);

    public static void main(String[] args) throws LoginException, InterruptedException, RateLimitedException {
        instance = new Rixa();
        shardsList = new LinkedList<>();
        int shards = 3;
        for(int i = 0; i < shards; i++) {
            Logger.getLogger("Rixa").info("Loading shard #" + i);
            JDABuilder jda = new JDABuilder(AccountType.BOT)
                    .setToken("MjkxNTM5Njg2NTEyNTI1MzMy.DAZKfQ.kIHSmuCJHhklyC3gBAi0c_VKp-w")
                    .setEventManager(new AnnotatedEventManager())
                    .addEventListener(new MessageEvent())
                    .addEventListener(new BotEvent())
                    .setGame(Game.of("Rixa 1.0 | In Dev", "http://rixa.io"))
                    .setAutoReconnect(true)
                    .setStatus(OnlineStatus.ONLINE)
                    .setAudioEnabled(true)
                    .useSharding(i, shards);
            shardsList.add(jda.buildBlocking());
            getInstance().getLogger().info("Shard #" + i + " has been loaded");
        }
        timeUp = System.currentTimeMillis();
        register(new CommandExec[] {
                new InfoCommand(), new ServerInfoCommand(), new HelpCommand(),
                new DeleteMessagesCommand(), new PingCommand(), new PurgeCommand(),
                new BatchMoveCommand() });
        ReactionManager.registerReaction(new HelpReaction());
    }

    private static void register(CommandExec commandExecs[]) {
        for (CommandExec command: commandExecs) {
            CommandHandler.registerCommand(command);
        }
    }

    public static Rixa getInstance() {
        return instance;
    }

    public long getTimeUp() {
        return timeUp;
    }

    public Logger getLogger() {
        return Logger.getLogger("Rixa");
    }
}
