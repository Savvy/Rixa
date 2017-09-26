package me.savvy.rixa.events;

import me.savvy.rixa.Rixa;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateRegionEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuildEvent {

    @SubscribeEvent
    public void onNameUpdate(GuildUpdateNameEvent event) {
        try {
            PreparedStatement ps = Rixa.getDatabase().getConnection().get().prepareStatement("UPDATE `core` SET `guild_name` = ? WHERE `core`.`guild_id` = ?;");
            ps.setString(1, event.getGuild().getName());
            ps.setString(2, event.getGuild().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onIconUpdate(GuildUpdateIconEvent event) {
        try {
            PreparedStatement ps = Rixa.getDatabase().getConnection().get().prepareStatement("UPDATE `core` SET `icon` = ? WHERE `core`.`guild_id` = ?;");
            ps.setString(1, event.getGuild().getIconId());
            ps.setString(2, event.getGuild().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onOwnerUpdate(GuildUpdateOwnerEvent event) {
        try {
            PreparedStatement ps = Rixa.getDatabase().getConnection().get().prepareStatement("UPDATE `core` SET `guild_owner` = ? WHERE `core`.`guild_id` = ?;");
            ps.setString(1, event.getGuild().getOwner().getUser().getName());
            ps.setString(2, event.getGuild().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRegionUpdate(GuildUpdateRegionEvent event) {
        try {
            PreparedStatement ps = Rixa.getDatabase().getConnection().get().prepareStatement("UPDATE `core` SET `guild_region` = ? WHERE `core`.`guild_id` = ?;");
            ps.setString(1, event.getGuild().getRegion().getName());
            ps.setString(2, event.getGuild().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
