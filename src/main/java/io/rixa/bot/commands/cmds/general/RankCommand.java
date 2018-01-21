package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.LevelsModule;
import io.rixa.bot.pagination.ObjectPagination;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.user.manager.UserManager;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RankCommand extends Command {

    public RankCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        if (!rixaGuild.getModule("Levels").isEnabled()) {
            MessageFactory.create("Levels are not enabled on this server!")
                    .setColor(member.getColor()).queue(channel);
            return;
        }
        if (args.length == 0) {
            getInfo(rixaGuild, member).queue(channel);
            return;
        }
        List<Member> members = DiscordUtils.memberSearch(guild, String.join(" ", args), false);
        if (members.isEmpty()) {
            MessageFactory.create("Could not find valid member! Please try again!").setColor(member.getColor()).queue(channel);
            return;
        }
        getInfo(rixaGuild, members.get(0)).queue(channel);
    }

    private MessageFactory getInfo(RixaGuild rixaGuild, Member member) {
        User author = member.getUser();
        int rank = 1;
        int count = DatabaseAdapter.getInstance().get().queryForObject
                ("SELECT COUNT(*) FROM `levels`", Integer.class);
        if (count > 0) {
            rank = DatabaseAdapter.getInstance().get().queryForObject("SELECT * FROM `levels` WHERE `guild_id` = ? ORDER BY `experience` DESC",
                    new Object[]{member.getGuild().getId()}, (resultSet, i) -> {
                        int main = 1;

                        while (resultSet.next()) {
                            if (resultSet.getString("user_id").equalsIgnoreCase(member.getUser().getId())) {
                                return main;
                            }
                            main++;
                        }
                        return main;
                    });
        }
        RixaUser rixaUser = UserManager.getInstance().getUser(member.getUser());
        int levels = rixaUser.getLevels(rixaGuild.getGuild().getId());
        return MessageFactory.create()
                .setAuthor(author.getName(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl())
                .setTitle(author.getName() + "'s level")
                .setColor(member.getColor())
                .addField("Rank", String.valueOf(rank), true)
                .addField("Level", String.valueOf(DiscordUtils.getLevelFromExperience(rixaUser.getLevels
                        (rixaGuild.getId()))), true)
                .addField("Exp Needed",
                        DiscordUtils.getRemainingExperience(levels) + "/" + DiscordUtils.getNeededXP
                                (DiscordUtils.getLevelFromExperience(levels)).intValue(), false)
                .addField("Total Exp", String.valueOf(levels), true);
    }
}
