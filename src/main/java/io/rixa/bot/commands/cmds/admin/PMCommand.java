package io.rixa.bot.commands.cmds.admin;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.requests.ErrorResponse;

public class PMCommand extends Command {

    @Getter private RixaPermission rixaPermission;
    public PMCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
        this.rixaPermission = rixaPermission;
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        String msg = String.join(" ", args);
        Role role = DiscordUtils.getMentionedRole(guild, msg);
        if (role == null) {
            MessageFactory.create(String.format
                    ("You must mention a role to private message! Example: `%s @role this is a test private message!`",
                            commandLabel)).queue(channel);
            return;
        }
        msg = msg.replaceFirst(role.getAsMention(), "").replaceFirst("@" + role.getName(),"");
        int usersWithRole = 0;
        int sendingFailed = 0;
        for (Member memberWithRole : guild.getMembersWithRoles(role)) {
            try {
                memberWithRole.getUser().openPrivateChannel().complete().sendMessage(msg).queue();
                usersWithRole++;
            } catch (ErrorResponseException ex) {
                if (ex.getErrorResponse() == ErrorResponse.CANNOT_SEND_TO_USER)
                    sendingFailed++;
            }
        }
        MessageFactory.create("```" + msg + "```")
                .footer("Successful Deliveries: " + usersWithRole + " | Failed Deliveries: " + sendingFailed, guild.getIconUrl()).queue(channel);
    }
}