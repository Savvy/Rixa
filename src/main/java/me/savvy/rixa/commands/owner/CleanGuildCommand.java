package me.savvy.rixa.commands.owner;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.data.database.sql.DatabaseManager;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CleanGuildCommand implements CommandExec {

    @Override
    @Command(mainCommand = "cleanguilds",
            description = "Clean Inactive Guilds From Rixa's Database!",
            channelType = ChannelType.TEXT,
            showInHelp = false,
            type = CommandType.BOT_OWNER)
    public void execute(GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equalsIgnoreCase("202944101333729280")) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.")
                    .setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        new MessageBuilder("Cleaning...").setColor(event.getMember().getColor()).queue(event.getChannel());
        int cleaned = 0;
        DatabaseManager dbManager = Rixa.getDbManager();
        ResultSet rs = dbManager.executeQuery("SELECT * FROM `core`;");
        try {
            while (rs.next()) {
                if (event.getJDA().getGuildById(rs.getString("guild_id")) == null) {
                    cleaned++;
                    String id = rs.getString("guild_id");
                    //`core`, `levels`, `settings`, `music`, `modules`, `permissions`
                    if (checkExists("core", id))
                    dbManager.executeUpdate("DELETE FROM `core` WHERE `guild_id` = " + id);

                    if (checkExists("levels", id))
                    dbManager.executeUpdate("DELETE FROM `levels` WHERE `guild_id` = " + id);

                    if (checkExists("settings", id))
                    dbManager.executeUpdate("DELETE FROM `settings` WHERE `guild_id` = " + id);

                    if (checkExists("music", id))
                    dbManager.executeUpdate("DELETE FROM `music` WHERE `guild_id` = " + id);

                    if (checkExists("modules", id))
                    dbManager.executeUpdate("DELETE FROM `modules` WHERE `guild_id` = " + id);

                    if (checkExists("permissions", id))
                    dbManager.executeUpdate("DELETE FROM `permissions` WHERE `guild_id` = " + id);
                }
            }
            rs.getStatement().close();
            rs.close();
            new MessageBuilder(event.getAuthor().getAsMention() + ", successfully cleaned " + cleaned + " guilds from the database").setColor(event.getMember().getColor()).queue(event.getChannel());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkExists(String table, String guildId) {
        /*Result r = Result.ERROR;
        try {
            r = Rixa.getDbManager().checkExists("SELECT `" + guildId + "` FROM `" + table + "` WHERE `guild_id` = '" + guildId + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r == Result.TRUE;*/
        return true;
    }
}
