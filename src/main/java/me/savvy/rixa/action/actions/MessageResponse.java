package me.savvy.rixa.action.actions;

import me.savvy.rixa.action.Action;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by savit on 6/24/2017.
 */
public class MessageResponse implements Action {
    @Override
    public String getName() {
        return "MessageResponse";
    }

    @Override
    public String getDescription() {
        return "Upon trigger, responds with messages";
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        new MessageBuilder("MessageResponse action is still in development.")
                .setColor(event.getMember().getColor()).queue(event.getChannel());
    }

    public void execute() {}
}
