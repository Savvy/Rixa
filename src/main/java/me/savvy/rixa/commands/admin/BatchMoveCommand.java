package me.savvy.rixa.commands.admin;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 */
public class BatchMoveCommand implements CommandExec {
    @Override
    @Command(mainCommand = "batchmove",
            aliases = "bmove",
            description = "Move users within one role to another!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
         RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        if(!rixaGuild.hasPermission(event.getMember(), RixaPermission.BATCH_MOVE)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        if(event.getMessage().getMentionedRoles().size() < 2) {
            new MessageBuilder("You need to include two roles!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        Role old_role = event.getMessage().getMentionedRoles().get(0);
        Role new_role = event.getMessage().getMentionedRoles().get(1);
        List<Member> userWithRole = event.getGuild().getMembersWithRoles(old_role);
        if(userWithRole.size() == 0) {
            new MessageBuilder("There are no users with the role " + old_role.getAsMention()).setColor(old_role.getColor()).queue(event.getChannel());
            return;
        }
        new MessageBuilder("Moving **" + userWithRole.size() + "** users with role: " + old_role.getAsMention()
                + " to " + new_role.getAsMention()).setColor(old_role.getColor()).queue(event.getChannel());
        int success = 0;
        for(Member member: userWithRole) {
            try {
                event.getGuild().getController().modifyMemberRoles
                        (member, Collections.singletonList(new_role), Collections.singletonList(old_role)).queue();
                success++;
            } catch(PermissionException ex) {
                new MessageBuilder("I do not have permission to modify " + member.getAsMention() + "'s role").setColor(Color.RED).queue(event.getChannel());
            }
        }
        if(success > 0) {
            new MessageBuilder("Successfully moved **" + success + "** users to role " +
                    new_role.getAsMention()).setColor(new_role.getColor()).queue(event.getChannel());
        }
    }
}