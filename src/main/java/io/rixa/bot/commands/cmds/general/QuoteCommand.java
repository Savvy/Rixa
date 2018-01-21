package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import io.rixa.bot.utils.WebUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;

import java.io.IOException;

public class QuoteCommand extends Command {

    public QuoteCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        String[] quote = getAdvice();
        MessageFactory.create(quote[0]).setTitle("Author: " + quote[1]).footer("Requested by: " + member.getEffectiveName(),
                member.getUser().getEffectiveAvatarUrl()).setTimestamp()
                .setColor(member.getColor()).queue(channel);
    }

    private String[] getAdvice() {
        String[] strings = new String[2];
        String json;
        try {
            json = WebUtil.getWebPage("https://api.forismatic.com/api/1.0/?method=getQuote&lang=en&format=json");
        } catch (IOException e) {
            strings[0] = "Could not find any quotes for you.";
            strings[1] = "Author not found";
            return strings;
        }

        JSONObject obj = new JSONObject(json);
        String quote = obj.getString("quoteText");
        String author = obj.getString("quoteAuthor");
        strings[0] = quote;
        strings[1] = author;
        return strings;
    }
}
