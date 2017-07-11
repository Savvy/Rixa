package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.UrbanDictionary;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by savit on 6/24/2017.
 */
public class UrbanDictionaryCommand implements CommandExec {


    @Override
    @Command(mainCommand = "urbandictionary",
            aliases = {"ud"},
            description = "Search urban dictionary for a command!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        String[] message = event.getMessage().getContent().split(" ");
        String search = getMessage(message, 1);
        UrbanDictionary ud = null;
        try {
            ud = new UrbanDictionary(URLEncoder.encode(search, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(ud == null) {
            new MessageBuilder("Search term not found.").setColor(event.getMember().getColor()).queue(event.getChannel());
        }
        try {
            if(!ud.search()) {
                new MessageBuilder("Search term not found.").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
        } catch (IOException e) {
            new MessageBuilder("Search term not found.").setColor(event.getMember().getColor()).queue(event.getChannel());
        }
        new MessageBuilder(ud.getDefinition()).setTitle(String.format("Definition: %s", ud.getWordToSearch())).setColor(event.getMember().getColor())
                .addThumbnail("https://s-media-cache-ak0.pinimg.com/originals/f2/aa/37/f2aa3712516cfd0cf6f215301d87a7c2.jpg").queue(event.getChannel());
    }

    private String getMessage(String[] messages, int argToBegin) {
        StringBuilder builder = new StringBuilder();
        for(int i = argToBegin; i < messages.length; i++) {
            builder.append(messages[i]).append(" ");
        }
        return builder.toString().trim();
    }
}
