package me.savvy.rixa.commands.admin;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.Utils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.List;

/**
 * Created by savit on 7/7/2017.
 */
public class AddRoleCommand implements CommandExec {

    @Command(
            description = "Give a user a role",
            type = CommandType.ADMIN,
            channelType = ChannelType.TEXT,
            usage = "%paddrole", mainCommand = "addrole", aliases = {"ar", "addroles", "ars"})
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        if(!rixaGuild.hasPermission(event.getMember(), RixaPermission.ADD_ROLE)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        String[] messages = event.getMessage().getContent().split(" ");
        List<Member> memberList = Utils.memberSearch(event.getGuild(), event.getMessage().getContent(), true);
        List<Role> roleList = Utils.roleSearch(event.getGuild(), event.getMessage().getContent());
        if (messages.length >= 3) {
            if(memberList.size() < 1 ||
                    roleList.size() < 1) {
                new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " <role> <user>].").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            try {
                int users = event.getMessage().getMentionedUsers().size();
                memberList.forEach(user -> event.getGuild().getController().addRolesToMember(user, roleList).queue());
                new MessageBuilder("Successfully given " + users + " `" + roleList.size() + "` role(s)").setColor(event.getMember().getColor()).queue(event.getChannel());
            } catch (PermissionException ex) {
                new MessageBuilder(event.getMember().getAsMention() + ", sorry I do not have permission for this!").setColor(event.getMember().getColor()).queue(event.getChannel());
            }
        } else {
            new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " <role> <user>].").setColor(event.getMember().getColor()).queue(event.getChannel());
        }
    }
}
