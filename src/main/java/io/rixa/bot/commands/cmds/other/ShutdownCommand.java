package io.rixa.bot.commands.cmds.other;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ShutdownCommand extends Command {

    public ShutdownCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        if (!(Rixa.getInstance().getConfiguration().isBotAdmin(member.getUser().getId()))) {
            new MessageFactory(member.getAsMention()
                    + ", you do not have permission for this command.").setColor(member.getColor()).queue(channel);
            return;
        }
        try {
            MessageFactory.create("Shutting down...").selfDestruct(0).queue(channel);
            for (RixaGuild rixaGuild : GuildManager.getInstance().getGuilds().values()) {
                Thread.sleep(50);
                rixaGuild.save();
            }
            Thread.sleep(500);
            Rixa.getInstance().close();
            System.exit(0);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        Rixa.getInstance().close();
    }
}
