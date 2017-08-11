package me.savvy.rixa.events;

import com.mysql.jdbc.StringUtils;
import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.handlers.CommandRegistrar;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.reactions.handlers.ReactRegistrar;
import me.savvy.rixa.modules.reactions.handlers.ReactionManager;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timber on 5/7/2017.
 */
public class MessageEvent {

    private final Pattern INVITE = Pattern.compile("discord(?:\\.gg|app.com\\/invite)\\/([A-Z0-9-]{2,16})",Pattern.CASE_INSENSITIVE);


    @SubscribeEvent
    public void handle(GuildMessageReceivedEvent event) {
        if (event.getGuild() == null) return;
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        String prefix = rixaGuild
                .getGuildSettings()
                .getPrefix();
        //checkMessage(event.getMessage());
        if (!event.getMessage().getContent().startsWith(prefix))  {
            if (!(rixaGuild.getLevelsModule().isEnabled())) {
                return;
            }
            /*if(!event.getAuthor().getId().equalsIgnoreCase("202944101333729280") &&
                    !event.getAuthor().getId().equalsIgnoreCase("207322957075185665")) {
                return;
            }*/
            if(rixaGuild.getLevelsModule().getUserData(event.getAuthor().getId()).awardIfCan()) {
                new MessageBuilder(event.getAuthor().getAsMention() + " has leveled up to level " +
                        rixaGuild.getLevelsModule().getUserData(event.getAuthor().getId()).getLevel())
                        .setColor(event.getMember().getColor()).queue(event.getChannel());
            }
            return;
        }

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

    private void checkMessage(Message message) {
        List<String> invites = new ArrayList<>();
        Matcher matcher = INVITE.matcher(message.getRawContent());
        while(matcher.find()) {
            invites.add(matcher.group(1));
        }
        if(invites.size() == 0) {
            return;
        }
        for(String inviteCode : invites) {
            Invite invite = null;
            try {
                invite = Invite.resolve(message.getJDA(), inviteCode).complete();
            } catch(Exception e) {}
            if(invite !=null && !invite.getGuild().getId().equals(message.getGuild().getId())) {
                new MessageBuilder(String.format("Advertising is not allowed, %s!",
                        message.getAuthor().getAsMention())).setColor(message.getMember().getColor()).queue(message.getTextChannel());
                message.delete().reason("Advertising is not allowed!").queue();
            }
        }
    }

    @SubscribeEvent
    public void onMemberJoin(PrivateMessageReceivedEvent event) {
        RixaGuild rixaGuild;
        if (MemberEvent.joinMembers.containsKey(event.getAuthor().getId())) {
            rixaGuild = RixaGuild.getGuild(MemberEvent.joinMembers.get(event.getAuthor().getId()));
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