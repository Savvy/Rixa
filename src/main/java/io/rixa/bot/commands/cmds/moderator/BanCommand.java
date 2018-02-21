package io.rixa.bot.commands.cmds.moderator;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import jdk.nashorn.internal.objects.annotations.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class BanCommand extends Command {
    
    RixaPermission permission;
    
    public BanCommand(String command, RixaPermission permission, String descriptopn) {
        super(command, permission, descriptopn);
        this.permission = permission;
    }
    
    @Override
    public void execute(String command, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        if (args.length < 1) {
            MessageFactory.create(
                    String.format("Incorrect Usage! Example: `%s%s @User`", rixaGuild.getSettings().getPrefix(),
                            command)).setColor(member.getColor()).queue(channel);
            
        }
    }
    
}
