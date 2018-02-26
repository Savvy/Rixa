package io.rixa.bot.reactions;

import lombok.Getter;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public abstract class React {

  @Getter private final String name;

  public React(String name) {
    this.name = name;
  }

  @SubscribeEvent
  public abstract void onReact(MessageReactionAddEvent event);
}
