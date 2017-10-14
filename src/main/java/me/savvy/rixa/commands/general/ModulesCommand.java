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
public class ModulesCommand implements CommandExec {

    @Override
    @Command(mainCommand = "modules",
            description = "List Rixa Modules!",
            aliases = {"module"},
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        new MessageBuilder("Available Rixa Modules: " + String.join(", ", rixaGuild.getModules().keySet())).setColor(event.getMember().getColor()).complete(event.getChannel());
    }
}
