package me.savvy.rixa.commands.owner;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckGuildCommand implements CommandExec {

    @Override
    @Command(mainCommand = "checkguild",
            description = "Update Rixa's database!",
            channelType = ChannelType.TEXT,
            showInHelp = false,
            type = CommandType.BOT_OWNER,
            aliases = { "cg", "checkg", "guildcheck", "gc"})
    public void execute(GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equalsIgnoreCase("202944101333729280")) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.")
                    .setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        new MessageBuilder(event.getAuthor().getAsMention() + ", updating guild!").setColor(event.getMember().getColor()).queue(event.getChannel());
        int updated = 0;
        for (Member member: event.getGuild().getMembers()) {
            if (member.getUser().getId().equalsIgnoreCase(event.getAuthor().getId())) continue;
            try {
                if (!(checkExists(member.getUser().getId()))) {
                    PreparedStatement ps = Rixa.getDatabase().getPreparedStatement("INSERT INTO `user` (`user_id`, `user_name`, `avatar_hash`) VALUES (?, ?, ?)");
                    ps.setString(1, member.getUser().getId());
                    ps.setString(2, member.getUser().getName());
                    ps.setString(3, member.getUser().getAvatarId());
                    ps.executeUpdate();
                    updated++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        new MessageBuilder(event.getAuthor().getAsMention() + ", successfully updated " + updated + " out of " +
                event.getGuild().getMembers().size() + " users in database.").setColor(event.getMember().getColor()).queue(event.getChannel());
    }

    private boolean checkExists(String userId) {
        try {
            PreparedStatement ps = Rixa.getDatabase().getPreparedStatement("SELECT `user_id` FROM `user` WHERE `user_id` = ?;");
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
