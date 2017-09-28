package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.modules.levels.LevelsModule;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class LeaderboardCommand implements CommandExec {

    @Override
    @Command(mainCommand = "leaderboard",
            description = "Check your ping!",
            aliases = {"leaderboards", "levels"},
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        if (!((LevelsModule) rixaGuild.getModule("Levels")).isEnabled()) {
            new MessageBuilder("Levels are not enabled on this server!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        Message message = event.getChannel().sendMessage
                (((LevelsModule) rixaGuild.getModule("Levels")).leaderboard
                        (event.getMember(), 1).getBuilder().build()).complete();
        message.addReaction("\u2B05").complete();
                message.addReaction("\u27A1").complete();
    }
}
