package io.rixa.bot.reactions;

import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public interface React {

  void onReact(GuildMessageReactionAddEvent event);

  String getName();
}
