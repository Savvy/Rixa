package me.savvy.rixa.commands.admin;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.managers.GuildManager;
import net.dv8tion.jda.core.requests.restaction.InviteAction;

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
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        if(!rixaGuild.hasPermission(event.getMember(), RixaPermission.ADD_ROLE)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        String[] messages = event.getMessage().getContent().split(" ");
        if (messages.length >= 3) {
            if(event.getMessage().getMentionedRoles().size() < 1 ||
                    event.getMessage().getMentionedUsers().size() < 1) {
                new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " <role> <user>].").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            try {
                List<Role> roles = event.getMessage().getMentionedRoles();
                User user = event.getMessage().getMentionedUsers().get(0);
                event.getGuild().getController().addRolesToMember(event.getGuild().getMember(user), roles).queue();
                new MessageBuilder("Successfully given " + user.getAsMention() + " `" + roles.size() + "` role(s)").setColor(event.getMember().getColor()).queue(event.getChannel());
            } catch (PermissionException ex) {
                new MessageBuilder(event.getMember().getAsMention() + ", sorry I do not have permission for this!").setColor(event.getMember().getColor()).queue(event.getChannel());
            }
        } else {
            new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " <role> <user>].").setColor(event.getMember().getColor()).queue(event.getChannel());
        }
    }
}
