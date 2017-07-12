package me.savvy.rixa.events;

import com.mysql.jdbc.StringUtils;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.handlers.CommandRegistrar;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.RixaManager;
import me.savvy.rixa.modules.reactions.handlers.ReactRegistrar;
import me.savvy.rixa.modules.reactions.handlers.ReactionManager;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * Created by Timber on 5/7/2017.
 */
public class MessageEvent {

    @SubscribeEvent
    public void handle(GuildMessageReceivedEvent event) {
        if (event.getGuild() == null) return;
        RixaGuild rixaGuild = RixaManager.getGuild(event.getGuild());
        String prefix = rixaGuild
                .getGuildSettings()
                .getPrefix();
        if (!event.getMessage().getContent().startsWith(prefix))  {return; }

        String[] splitContent = event.getMessage().getContent().replace(prefix, "").split(" ");
        if(!CommandHandler.hasCommand(splitContent[0])) {
            return; }
        CommandRegistrar cmd = CommandHandler.get(splitContent[0]);
        Method m = cmd.getMethod();
        try {
            m.invoke(cmd.getExecutor(), event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onMemberJoin(PrivateMessageReceivedEvent event) {
        RixaGuild rixaGuild;
        if (MemberEvent.joinMembers.containsKey(event.getAuthor().getId())) {
            rixaGuild = RixaManager.getGuild(MemberEvent.joinMembers.get(event.getAuthor().getId()));
            if (event.getMessage().getContent().equalsIgnoreCase("I agree") ||
                    event.getMessage().getContent().equalsIgnoreCase("I accept")
                    || event.getMessage().getContent().equalsIgnoreCase("Yes")) {
                MemberEvent.joinMembers.remove(event.getAuthor().getId());
                if (!rixaGuild.getGuildSettings().getDefaultRole().equalsIgnoreCase("default_value")) {
                    try {
                        rixaGuild.getGuild().getController().addRolesToMember(rixaGuild.getGuild()
                                .getMember(event.getAuthor()), Collections.singleton(rixaGuild.getGuild().getRoleById(rixaGuild.getGuildSettings().getDefaultRole()))).complete();
                        new MessageBuilder(String.format("You have been promoted on %s!", rixaGuild.getGuild().getName()))
                                .setColor(rixaGuild.getGuild().getMember(event.getAuthor()).getColor()).send(event.getAuthor());
                    } catch(PermissionException ex) {
                            new MessageBuilder(String.format("I do not have permission for %s in %s", ex.getPermission().getName(), rixaGuild.getGuild().getName()))
                                    .setColor(Color.RED).send(rixaGuild.getGuild().getOwner().getUser());
                    }
                }
            } else if (event.getMessage().getContent().equalsIgnoreCase("I disagree") ||
                    event.getMessage().getContent().equalsIgnoreCase("I deny") ||
                    event.getMessage().getContent().equalsIgnoreCase("No")) {
                try {
                    new MessageBuilder(String.format("You have been removed from %s because you did not agree to the terms!", rixaGuild.getGuild().getName()))
                            .setColor(Color.RED).send(event.getAuthor());
                    MemberEvent.joinMembers.remove(event.getAuthor().getId());
                    rixaGuild.getGuild().getController().kick(rixaGuild.getGuild().getMember(event.getAuthor())).complete();
                } catch (PermissionException ex) {
                    if(ex.getPermission() == Permission.KICK_MEMBERS) {
                        new MessageBuilder(String.format("I do not have permission to kick %s from %s", event.getAuthor().getName(), rixaGuild.getGuild().getName()))
                                .setColor(Color.RED).send(rixaGuild.getGuild().getOwner().getUser());
                    } else {
                        new MessageBuilder(String.format("I do not have permission for %s in %s", ex.getPermission().getName(), rixaGuild.getGuild().getName()))
                                .setColor(Color.RED).send(rixaGuild.getGuild().getOwner().getUser());
                    }
                }
            }
        } else {
            if(!event.getAuthor().isBot())
                new MessageBuilder("Private messages are currently disabled!").setColor(Color.RED).send(event.getAuthor());
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
