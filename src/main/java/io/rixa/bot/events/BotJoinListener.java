package io.rixa.bot.events;

import io.rixa.bot.guild.manager.GuildManager;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class BotJoinListener {

    @SubscribeEvent
    public void onJoin(GuildJoinEvent event) {
        System.out.println("GuildJoinEvent Event");
        event.getJDA().getGuilds().forEach(guild -> GuildManager.getInstance().addGuild(guild));
    }
}
