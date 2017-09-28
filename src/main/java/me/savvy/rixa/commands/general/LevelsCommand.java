package me.savvy.rixa.commands.general;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.guild.user.UserData;
import me.savvy.rixa.modules.levels.LevelsModule;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by savit on 7/11/2017.
 */
public class LevelsCommand implements CommandExec {

    @Override
    @Command(mainCommand = "rank",
            description = "View your levels!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        if (!((LevelsModule)  rixaGuild.getModule("Levels")).isEnabled()) {
            new MessageBuilder("Levels are not enabled on this server!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        
        if (event.getMessage().getContent().split(" ").length == 2) {
            if(event.getMessage().getMentionedUsers().size() < 1) {
                new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + rixaGuild.getGuildSettings().getPrefix() + "rank <user>].").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            if (event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0)) == null) {
                new MessageBuilder(event.getMember().getAsMention() + ", couldn't find user.").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            getInfo(rixaGuild, event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0))).queue(event.getChannel());
            return;
        }
        getInfo(rixaGuild, event.getMember()).queue(event.getChannel());
    }
    
    public MessageBuilder getInfo(RixaGuild rixaGuild, Member member) {
        User author = member.getUser();
        UserData data = ((LevelsModule)  rixaGuild.getModule("Levels")).getUserData(author.getId());
        String query = "SELECT * FROM `levels` WHERE `guild_id` = '" + rixaGuild.getGuild().getId() + "' ORDER BY `experience` DESC";
        ResultSet rs = null;
        try {
            rs = Rixa.getDatabase().getConnection().get().prepareStatement(query).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int rowNumber = 0;
        String rank = "Not found.";
        try {
            rs.beforeFirst();
            while (rs.next()) {
                rowNumber++;
                if (rs.getString("user_id").equalsIgnoreCase(author.getId())) {
                    rank = String.valueOf(rowNumber);
                    break;
                }
            }
            rs.close();
        } catch(SQLException ignored) {
            ignored.printStackTrace();
        }
       return new MessageBuilder()
                .setAuthor(author.getName(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl())
                .setTitle(author.getName() + "'s level")
                .setColor(member.getColor())
                .addField("Rank", rank, true)
                .addField("Level", String.valueOf(data.getLevel()), true)
                .addField("Exp Needed",
                        data.getRemainingExperience() + "/" + data.getNeededXP
                                (data.getLevelFromExperience(data.getExperience())).intValue(), false)
                .addField("Total Exp", String.valueOf(data.getExperience()), true);
    }
}
