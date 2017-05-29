package me.savvy.rixa.events;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.RixaManager;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * Created by Timber on 5/23/2017.
 */
public class BotEvent {

    @SubscribeEvent
    public void onJoin(GuildJoinEvent event) {
        new RixaGuild(event.getGuild());
    }

    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        try {
            Rixa.getInstance().getLogger().info("Successfully loaded...");
            event.getJDA().getGuilds().forEach(RixaGuild::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onQuit(GuildLeaveEvent event) {
        RixaManager.removeGuild(RixaManager.getGuild(event.getGuild()));
    }
}
