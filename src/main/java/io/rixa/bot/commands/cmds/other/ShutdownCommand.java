package io.rixa.bot.commands.cmds.other;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ShutdownCommand extends Command {

    public ShutdownCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        if (!(Rixa.getInstance().getConfiguration().isBotAdmin(event.getAuthor().getId()))) {
            new MessageFactory(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        try {
            MessageFactory.create("Shutting down...").queue(event.getChannel());
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
