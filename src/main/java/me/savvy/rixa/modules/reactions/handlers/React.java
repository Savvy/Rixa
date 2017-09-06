package me.savvy.rixa.modules.reactions.handlers;

import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

/**
 * Created by Timber on 5/7/2017.
 */
public interface React {

    void reactionTrigger(MessageReactionAddEvent event);

    default void reactionGuildTrigger(GuildMessageReactionAddEvent event) { }
}
