package me.savvy.rixa.commands.general;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.user.UserData;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.YoutubeSearch;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by savit on 7/11/2017.
 */
public class LevelsCommand implements CommandExec {

    @Override
    @Command(mainCommand = "rank",
            description = "View your levels!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        if (!rixaGuild.getLevelsModule().isEnabled()) {
            new MessageBuilder("Levels are not enabled on this server!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        UserData data = rixaGuild.getLevelsModule().getUserData(event.getAuthor().getId());
        String query = "SELECT * FROM `levels` WHERE `guild_id` = '" + event.getGuild().getId() + "' ORDER BY `experience` DESC";
        ResultSet rs = Rixa.getDbManager().executeQuery(query);
        int rowNumber = 0;
        String rank = "Not found.";
        try {
            rs.beforeFirst();
            while (rs.next()) {
                rowNumber++;
                if (rs.getString("user_id").equalsIgnoreCase(event.getAuthor().getId())) {
                    rank = String.valueOf(rowNumber);
                    break;
                }
            }
            rs.close();
        } catch(SQLException ignored) {
            ignored.printStackTrace();
        }
        new MessageBuilder()
                .setAuthor(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                .setTitle(event.getAuthor().getName() + "'s level")
                .setColor(event.getMember().getColor())
                .addField("Rank", rank, true)
                .addField("Level", String.valueOf(data.getLevel()), true)
                .addField("Exp Needed",
                        data.getRemainingExperience() + "/" + data.getNeededXP
                                (data.getLevelFromExperience(data.getExperience())).intValue(), false)
                .addField("Total Exp", String.valueOf(data.getExperience()), false)
                .queue(event.getChannel());
    }
}
