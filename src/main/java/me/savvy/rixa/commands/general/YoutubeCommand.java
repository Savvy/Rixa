package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
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
        String search = getMessage(message);
        try {
            YoutubeSearch ytSearch = new YoutubeSearch(search);
            event.getChannel().sendMessage(ytSearch.getUrl(0)).queue();/*
            new MessageBuilder(ytSearch.getUrl(0))
                    .setColor(event.getMember().getColor()).queue(event.getChannel());*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMessage(String[] messages) {
        StringBuilder builder = new StringBuilder() ;
        for(int i = 1; i < messages.length; i++) {
            builder.append(messages[i]).append(" ");
        }
        return builder.toString().trim();
    }
}
