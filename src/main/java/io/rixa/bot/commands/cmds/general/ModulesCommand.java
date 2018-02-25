package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class ModulesCommand extends Command {

    public ModulesCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        List<String> modules = new ArrayList<>();
        rixaGuild.getModules().values().forEach
                (module -> modules.add(String.format("%s [%s]", module.getName(),
                        (module.isEnabled()) ? "Enabled" : "Disabled")));
        MessageFactory.create(String.join(",\n", modules)).setAuthor("Module List", guild.getIconUrl())
                .setTimestamp().setColor(member.getColor()).queue(channel);
    }
}
