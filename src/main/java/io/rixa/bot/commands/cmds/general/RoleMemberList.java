package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class RoleMemberList extends Command {

    public RoleMemberList(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        String string = String.join(" ", args);
        Role role = DiscordUtils.searchFirstRole(guild, string);
        if (role == null) {
            MessageFactory.create("Incorrect Usage! Example: " + commandLabel + " Member!").setColor(member.getColor()).queue(channel);
            return;
        }
        List<Member> roleMembers = guild.getMembersWithRoles(role);
        if (roleMembers.isEmpty()) {
            MessageFactory.create("Could not find any users with the role " + role.getName()).setColor(member.getColor()).queue(channel);
            return;
        }
        List<String> membersWithRole = new ArrayList<>();
        roleMembers.forEach(roleMember -> membersWithRole.add(format(roleMember)));
        MessageFactory.create(String.join(", ", membersWithRole))
                .setAuthor("Users with role: " + role.getName(), guild.getIconUrl()).setTimestamp()
                .setColor(member.getColor()).queue(channel);
    }

    private String format(Member member) {
        String name = member.getUser().getName() + "#" + member.getUser().getDiscriminator();
        if (member.getNickname() != null && !member.getNickname().isEmpty()) {
            name = name + String.format(" [%s]", member.getNickname());
        }
        return name;
    }
}
