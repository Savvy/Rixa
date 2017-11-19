package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand extends Command {

    public PingCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        MessageFactory.create("Pong! [" + event.getJDA().getPing() + "ms]").setColor(event.getMember().getColor()).queue(event.getChannel());
    }
}
