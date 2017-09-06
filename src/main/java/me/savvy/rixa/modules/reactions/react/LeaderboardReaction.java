package me.savvy.rixa.modules.reactions.react;

import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.reactions.handlers.React;
import me.savvy.rixa.modules.reactions.handlers.ReactHandle;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

/**
 * Created by Timber on 6/9/2017.
 */
public class LeaderboardReaction implements React {

    @Override
    public void reactionTrigger(MessageReactionAddEvent event) { }

    @Override
    @ReactHandle(title = "Leaderboard", description = "Leaderboard for levels")
    public void reactionGuildTrigger(GuildMessageReactionAddEvent event) {
        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
        Guild guild = event.getGuild();
        if(guild == null) {
            return;
        }
        RixaGuild rixaGuild = RixaGuild.getGuild(guild);
        int page = 500;
        switch (event.getReaction().getEmote().getName()) {
            case "\u2B05":// previous
                page = Integer.parseInt(message.getEmbeds().get(0).getFooter().getText().split(" /")[0].replace("Page: (", "")) - 1;
                break;
            case "\u27A1":// next
                page = Integer.parseInt(message.getEmbeds().get(0).getFooter().getText().split(" /")[0].replace("Page: (", "")) + 1;
                break;
        }
        if(page != 500) {
            me.savvy.rixa.utils.MessageBuilder builder = rixaGuild.getLevelsModule().leaderboard
                    (event.getMember(), page);
            if (builder == null) return;
            message.editMessage(builder.getBuilder().build()).queue();
        }
    }
}