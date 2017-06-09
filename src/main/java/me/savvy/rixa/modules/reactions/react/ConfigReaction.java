package me.savvy.rixa.modules.reactions.react;

import me.savvy.rixa.modules.reactions.handlers.React;
import me.savvy.rixa.modules.reactions.handlers.ReactHandle;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

/**
 * Created by Timber on 6/9/2017.
 */
public class ConfigReaction implements React {

    @Override
    @ReactHandle(title = "Config", description = "Configuration Menu for Rixa")
    public void reactionTrigger(MessageReactionAddEvent event) {
    }
}