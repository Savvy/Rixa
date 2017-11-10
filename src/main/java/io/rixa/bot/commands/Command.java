package io.rixa.bot.commands;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Collections;
import java.util.List;

public abstract class Command {

    @Getter @Setter private String command, description;
    @Getter @Setter private RixaPermission permission;
    @Getter @Setter private List<String> aliases;
    public Command(String command) {
        this(command, RixaPermission.NONE, "Undefined", Collections.emptyList());
    }

    public Command(String command, RixaPermission rixaPermission) {
        this(command, rixaPermission, "Undefined", Collections.emptyList());
    }

    public Command(String command, RixaPermission rixaPermission, String description) {
        this(command, rixaPermission, description, Collections.emptyList());
    }

    public Command(String command, RixaPermission rixaPermission, String description, List<String> aliases) {
        setCommand(command);
        setPermission(rixaPermission);
        setDescription(description);
        setAliases(aliases);
    }

    public abstract boolean execute(GuildMessageReceivedEvent event);
}
