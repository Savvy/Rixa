package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.apis.UrbanDictionary;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrbanDictionaryCommand extends Command {

    public UrbanDictionaryCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) throws IOException {
        String searchQuery = String.join(" ", args);
        UrbanDictionary urbanDictionary = null;
        try {
            urbanDictionary = new UrbanDictionary(URLEncoder.encode(searchQuery, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(urbanDictionary == null || !urbanDictionary.search()) {
            MessageFactory.create("Search term not found.").setColor(member.getColor()).queue(channel);
            return;
        }
        MessageFactory.create(urbanDictionary.getDefinition()).setAuthor(
                String.format("Definition: %s", urbanDictionary.getWordToSearch()),
                "https://s-media-cache-ak0.pinimg.com/originals/f2/aa/37/f2aa3712516cfd0cf6f215301d87a7c2.jpg").setColor(member.getColor()).queue(channel);
    }
}
