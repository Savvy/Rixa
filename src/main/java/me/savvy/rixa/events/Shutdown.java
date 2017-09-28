package me.savvy.rixa.events;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * Created by Timber on 5/23/2017.
 */
public class Shutdown {

    @SubscribeEvent
    public void onShutdown(ShutdownEvent event) {
        System.out.println("Test");
        Rixa.getInstance().close();
    }
}
