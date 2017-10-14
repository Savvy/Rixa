package me.savvy.rixa.commands.owner;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.guild.user.profile.Profile;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;

public class CleanGuildCommand implements CommandExec {

    @Override
    @Command(mainCommand = "profile",
            description = "Profile",
            channelType = ChannelType.TEXT,
            type = CommandType.BOT_OWNER)
    public void execute(GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equalsIgnoreCase("202944101333729280")) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.")
                    .setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        try {
            event.getChannel().sendFile(Profile.getInstance().get(event.getMember()), null).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
