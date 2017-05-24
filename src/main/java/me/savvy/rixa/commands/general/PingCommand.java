package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by Timber on 5/23/2017.
 */
public class PingCommand implements CommandExec {
    @Override
    @Command(mainCommand = "ping",
            aliases = "",
            description = "Check your ping!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        new MessageBuilder("Pong!").setColor(event.getMember().getColor()).queue(event.getChannel());
    }
}
