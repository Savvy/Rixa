package me.savvy.rixa.commands.mod;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by Timber on 5/23/2017.
 */
public class RaidModeCommand implements CommandExec {

    @Override
    @Command(mainCommand = "toggleraidmode",
            description = "Toggle Raid Mode!",
            channelType = ChannelType.TEXT,
            aliases = {"raidmode", "trm", "toggleraid"})
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        if(!rixaGuild.hasPermission(event.getMember(), RixaPermission.TOGGLE_RAIDMODE)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        if (!(rixaGuild.getGuildSettings().isRaidMode())) {
            rixaGuild.getGuildSettings().startRaidMode();
            new MessageBuilder(event.getMember().getAsMention() + ", successfully turned raid mode on").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
            rixaGuild.getGuildSettings().endRaidMode();
            new MessageBuilder(event.getMember().getAsMention() + ", successfully turned raid mode off.").setColor(event.getMember().getColor()).queue(event.getChannel());
    }
}
