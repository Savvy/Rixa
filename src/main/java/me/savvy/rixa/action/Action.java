package me.savvy.rixa.action;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by savit on 6/24/2017.
 */
public interface Action {

    String getName();

    String getDescription();

    void execute();

    void execute(GuildMessageReceivedEvent event);
}
