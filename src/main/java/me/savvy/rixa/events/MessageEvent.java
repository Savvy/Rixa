package me.savvy.rixa.events;

import com.mysql.jdbc.StringUtils;
import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.handlers.CommandRegistrar;
import me.savvy.rixa.modules.reactions.handlers.ReactRegistrar;
import me.savvy.rixa.modules.reactions.handlers.ReactionManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.lang.reflect.Method;

/**
 * Created by Timber on 5/7/2017.
 */
public class MessageEvent {

    @SubscribeEvent
    public void handle(GuildMessageReceivedEvent event) {
        if (event.getGuild() == null) return;
        String prefix = "/";
        if (!event.getMessage().getContent().startsWith(prefix)) return;
     //   Map<String, CommandRegistrar> commands = CommandHandler.getCommands();
        String[] splitContent = event.getMessage().getContent().replace(prefix, "").split(" ");
        if(!CommandHandler.hasCommand(splitContent[0])) { return; }
        //if (!commands.containsKey(splitContent[0])) return;
        CommandRegistrar cmd = CommandHandler.get(splitContent[0]);
        //CommandRegistrar cmd = commands.get(splitContent[0]);
        Method m = cmd.getMethod();
        try {
            m.invoke(cmd.getExecutor(), event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onReact(MessageReactionAddEvent event) {
        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
        if(message == null || message.getEmbeds().size() != 1) return;
        MessageEmbed embed = message.getEmbeds().get(0);
        if(StringUtils.isNullOrEmpty(embed.getTitle())) return;
        String[] titleSplit = embed.getTitle().split(": ");
         if(ReactionManager.getReactions().containsKey(titleSplit[0])) {
             ReactRegistrar reactRegistrar = ReactionManager.getReactions().get(titleSplit[0]);
             Method m = reactRegistrar.getMethod();
             try {
                 m.invoke(reactRegistrar.getExecutor(), event);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
    }
}
