package io.rixa.commands.cmds;

import io.rixa.commands.Command;
import io.rixa.commands.RixaPermission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand extends Command {

    public HelpCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public boolean execute(GuildMessageReceivedEvent event) {
        return false;
    }
}
