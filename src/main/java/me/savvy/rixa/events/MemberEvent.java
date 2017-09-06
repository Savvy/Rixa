package me.savvy.rixa.events;

import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by savit on 6/18/2017.
 */
public class MemberEvent {

    public static Map<String, Guild> joinMembers = new HashMap<>();

    @SubscribeEvent
    public void onMember(GuildMemberJoinEvent event) {
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());

      /*  if (rixaGuild.getGuildSettings().isRaidMode()) {
            if(event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
                new MessageBuilder("Sorry " + event.getGuild().getName() + " currently has raid mode enabled. Please rejoin at a later time. Sorry!").send(event.getUser());
                        event.getGuild().getController().kick(event.getMember()).reason("Raid-Mode").queue();
            }
            return;
        }*/
        if(rixaGuild.getGuildSettings().getJoinMessageChannel() != null) {
            try {
                new MessageBuilder(rixaGuild.getGuildSettings().getJoinMessage()
                        .replace("{0}", event.getMember().getAsMention()).replace("{1}", event.getGuild().getName()).replace
                                ("{2}", String.valueOf(event.getGuild().getMembers().size()))).setColor
                        (event.getMember().getColor()).queue(rixaGuild.getGuildSettings().getJoinMessageChannel());
            } catch(PermissionException ex) {
                new MessageBuilder(String.format("I do not have permission for %s in %s", ex.getPermission().getName(), rixaGuild.getGuild().getName()))
                        .setColor(event.getMember().getColor()).send(rixaGuild.getGuild().getOwner().getUser());
                return;
            }
        }


        if(rixaGuild.getGuildSettings().isJoinVerification()) {
            joinMembers.put(event.getMember().getUser().getId(), event.getGuild());
        } else if(!rixaGuild.getGuildSettings().getDefaultRole().equalsIgnoreCase("default_value")) {
            try {
                Role role = event.getGuild().getRoleById(rixaGuild.getGuildSettings().getDefaultRole());
                event.getGuild().getController().addRolesToMember(event.getMember(), Collections.singleton(role)).complete();
                rixaGuild.getGuildSettings().setLastJoin(System.currentTimeMillis());
            } catch(PermissionException ex) {
                new MessageBuilder(String.format("I do not have permission for %s in %s", ex.getPermission().getName(), rixaGuild.getGuild().getName()))
                        .setColor(event.getMember().getColor()).send(rixaGuild.getGuild().getOwner().getUser());
                return;
            }
        }

        if(rixaGuild.getGuildSettings().getJoinPrivateMessage().equalsIgnoreCase("default")) {
            return;
        }
        String message = rixaGuild.getGuildSettings().getJoinPrivateMessage().replace("{0}", event.getMember().getAsMention())
                        .replace("{1}", event.getGuild().getName()).replace("{2}", String.valueOf
                        (event.getGuild().getMembers().size()));
        new MessageBuilder(message).setColor(event.getMember().getColor()).send(event.getMember().getUser());
    }
}
