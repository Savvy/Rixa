package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.apis.YoutubeSearch;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;

public class YoutubeCommand extends Command {

    public YoutubeCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        String searchQuery = String.join(" ", args);
        try {
            YoutubeSearch ytSearch = new YoutubeSearch(searchQuery);
            channel.sendMessage(ytSearch.getUrl(0)).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
