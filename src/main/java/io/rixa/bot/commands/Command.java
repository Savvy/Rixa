package io.rixa.bot.commands;

import io.rixa.bot.commands.perms.RixaPermission;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
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

 //   public abstract void execute(GuildMessageReceivedEvent event);

    public abstract void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) throws IOException;
}
