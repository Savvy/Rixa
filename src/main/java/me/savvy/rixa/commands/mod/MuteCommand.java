package me.savvy.rixa.commands.mod;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.Collections;

/**
 * Created by Timber on 6/5/2017.
 */
public class MuteCommand implements CommandExec {
    @Override
    @Command(mainCommand = "mute",
            description = "Mute a member.",
            type = CommandType.MOD,
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        if(!rixaGuild.hasPermission(event.getMember(), RixaPermission.MUTE)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }

        if(rixaGuild.getGuildSettings().getMuteRole() == null || rixaGuild.getGuildSettings().getMuteRole().equalsIgnoreCase("default_value")) {
            new MessageBuilder(event.getMember().getAsMention() + ", could not find appropriate role for muting!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        Role muteRole = event.getGuild().getRoleById(rixaGuild.getGuildSettings().getMuteRole());
        if(muteRole == null) {
            new MessageBuilder(event.getMember().getAsMention() + ", could not find appropriate role for muting!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }

        String[] messages = event.getMessage().getContent().split(" ");
        if(event.getMessage().getMentionedUsers().size() < 1) {
            new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " <user>].").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        try {
            for(User user: event.getMessage().getMentionedUsers()) {
                Member muted = rixaGuild.getGuild().getMember(user);
                if (rixaGuild.isUserMuted(muted.getUser())) {
                    rixaGuild.getGuild().getController().removeRolesFromMember(muted, Collections.singleton(muteRole)).queue();
                    rixaGuild.unmuteMember(muted.getUser());
                    new MessageBuilder("Successfully unmuted `" + muted.getEffectiveName() + "`.").setColor(event.getMember().getColor()).queue(event.getChannel());
                } else {
                    rixaGuild.getGuild().getController().addRolesToMember(muted, Collections.singleton(muteRole)).queue();
                    rixaGuild.muteMember(muted.getUser());
                    new MessageBuilder("Successfully muted `" + muted.getEffectiveName() + "`.").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            }
        } catch (PermissionException ex) {
            new MessageBuilder(event.getMember().getAsMention() + ", sorry I do not have permission for this!").setColor(event.getMember().getColor()).queue(event.getChannel());
        }
    }
}