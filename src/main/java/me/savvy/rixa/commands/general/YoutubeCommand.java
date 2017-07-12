package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.YoutubeSearch;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;

/**
 * Created by savit on 7/11/2017.
 */
public class YoutubeCommand implements CommandExec {

    @Override
    @Command(mainCommand = "youtube",
            aliases = {"yt"},
            description = "Search youtube for music videos!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        String[] message = event.getMessage().getContent().split(" ");
        String search = getMessage(message, 1);
        try {
            YoutubeSearch ytSearch = new YoutubeSearch(search);
            new MessageBuilder(ytSearch.getUrl(0))
                    .setColor(event.getMember().getColor()).queue(event.getChannel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMessage(String[] messages, int argToBegin) {
        StringBuilder builder = new StringBuilder() ;
        for(int i = argToBegin; i < messages.length; i++) {
            builder.append(messages[i]).append(" ");
        }
        return builder.toString().trim();
    }
}
