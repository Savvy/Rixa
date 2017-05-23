package me.savvy.rixa.commands.handlers;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by Timber on 5/7/2017.
 */
public interface CommandExec {

    public void execute(GuildMessageReceivedEvent event);
}
