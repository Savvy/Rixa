package io.rixa.bot.events;

import io.rixa.bot.guild.manager.GuildManager;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class ReadyListener {

    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        event.getJDA().getGuilds().forEach(guild -> GuildManager.getInstance().addGuild(guild));
    }
}
