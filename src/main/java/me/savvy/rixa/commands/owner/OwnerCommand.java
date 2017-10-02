package me.savvy.rixa.commands.owner;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class OwnerCommand implements CommandExec {

    @Override
    @Command(mainCommand = "shutdown",
            description = "Shutdown Rixa instance, save all data.",
            channelType = ChannelType.TEXT,
            showInHelp = false,
            type = CommandType.BOT_OWNER)
    public void execute(GuildMessageReceivedEvent event) {
        if (!Rixa.getConfig().getJsonObject().getJSONArray("botAdmins").toList().contains(event.getMember().getUser().getId())) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.")
                    .setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }

        try {
            new MessageBuilder("Shutting down...").queue(event.getChannel());
            for (RixaGuild rixaGuild : Guilds.getGuilds().values()) {
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
