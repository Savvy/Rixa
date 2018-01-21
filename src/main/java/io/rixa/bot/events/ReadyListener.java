package io.rixa.bot.events;

import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.user.manager.UserManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class ReadyListener {

  private ScheduledExecutorService scheduler;

  public ReadyListener() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
  }

  @SubscribeEvent
  public void onReady(ReadyEvent event) {
    if (event.getJDA().getGuilds().size() == 0) {
      return;
    }
    this.scheduler.scheduleWithFixedDelay(() -> {
      event.getJDA().getGuilds().forEach(guild ->
          GuildManager.getInstance().addGuild(guild).getModule("Levels").reload());
      UserManager.getInstance().getUserMap().values().forEach(RixaUser::save);
    }, 0, 5, TimeUnit.MINUTES);
  }
}
