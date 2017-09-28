package me.savvy.rixa.modules.reactions.react;

import me.savvy.rixa.commands.admin.ConfigCommand;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.modules.reactions.handlers.React;
import me.savvy.rixa.modules.reactions.handlers.ReactHandle;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

/**
 * Created by Timber on 6/9/2017.
 */
public class ConfigReaction implements React {

    @Override
    @ReactHandle(title = "Config", description = "Configuration Menu for Rixa")
    public void reactionTrigger(MessageReactionAddEvent event) {
        if (event.getChannel().getType() != ChannelType.PRIVATE
                || event.getUser().getId().equalsIgnoreCase(event.getJDA().getSelfUser().getId())) {
            return;
        }
        // "Page: (" + page
        // + "/ " + (maxPages - 2) + ")"
        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
        String title = message.getEmbeds().get(0).getTitle().split(": ")[1];
        Guild guild = event.getJDA().getGuildById(title);
        if(guild == null) {
            return;
        }
        RixaGuild rixaGuild = Guilds.getGuild(guild);
        String prefix = rixaGuild.getGuildSettings().getPrefix();
        MessageBuilder builder = null;
        int page = 500;
        switch (event.getReaction().getEmote().getName()) {
            case "\u2B05":// previous
                page = Integer.parseInt(message.getEmbeds().get(0).getFooter().getText().split(" /")[0].replace("Page: (", "")) - 1;
                break;
            case "\u27A1":// next
                page = Integer.parseInt(message.getEmbeds().get(0).getFooter().getText().split(" /")[0].replace("Page: (", "")) + 1;
                break;
        }
        System.out.println(page);
        if(page != 500) {
            builder = ConfigCommand.getInstance().sendHelp
                    (rixaGuild.getGuild().getMember(event.getUser()), page, prefix);
            message.editMessage(builder.getBuilder().build()).queue();
        }
    }
}