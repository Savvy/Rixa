package me.savvy.rixa.modules.reactions.react;

import me.savvy.rixa.modules.reactions.handlers.React;
import me.savvy.rixa.modules.reactions.handlers.ReactHandle;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

/**
 * Created by Timber on 5/7/2017.
 */
public class PollReaction implements React {

    @Override
    @ReactHandle(title = "Poll", description = "Host polls right from your discord server!")
    public void reactionTrigger(MessageReactionAddEvent event) {
        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
    }
}
