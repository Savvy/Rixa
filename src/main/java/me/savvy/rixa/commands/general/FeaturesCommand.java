package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by Timber on 5/23/2017.
 */
public class FeaturesCommand implements CommandExec {

    @Override
    @Command(mainCommand = "features",
            description = "List Rixa Features!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        String[] features = {};
        new MessageBuilder(
                features.length == 0 ? "There are currently no features listed." :
                "Rixa Features: " + String.join("\n", features
                )).setColor(event.getMember().getColor()).complete(event.getChannel());
    }
}
