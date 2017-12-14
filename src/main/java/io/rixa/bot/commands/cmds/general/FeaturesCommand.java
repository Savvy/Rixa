package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class FeaturesCommand extends Command {

    private String[] features = {
            "Music", "Economy", "Moderation", "Server List", "User Profiles",
            "Role Management", "Fun Commands", "Custom Commands", "Games", "& more."
    };

    public FeaturesCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        MessageFactory.create((features == null || features.length == 0) ? "There are currently no features listed." :
                "Rixa Features: " + String.join("\n", features
                )).setColor(member.getColor()).queue(channel);
    }
}
