package io.rixa.bot.events;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.exceptions.CommandNotFoundException;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.ConversationModule;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.io.IOException;

public class MessageListener {

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContent().trim();
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
        if (message.startsWith("@" + event.getGuild().getSelfMember().getEffectiveName())) {
            chatter(rixaGuild, event.getChannel(), message.replace("@" + event.getGuild().getSelfMember().getEffectiveName(), ""));
            return;
        }
        String prefix = "!";
        if (!(message.startsWith(prefix))) return;
        String[] msgArgs = message.split(" ");
        String commandName = (message.contains(" ") ? msgArgs[0] : message);
        String[] args = new String[msgArgs.length - 1];
        System.arraycopy(msgArgs, 1, args, 0, msgArgs.length - 1);
        command(commandName, prefix, event, args);
    }

    private void command(String commandName, String prefix, GuildMessageReceivedEvent event, String[] args) {
        commandName = commandName.replaceFirst(prefix, "");
        try {
            Command command = Rixa.getInstance().getCommandHandler().getCommand(commandName);
            //command.execute(event);
            event.getMessage().delete().queue();
            command.execute(commandName, event.getGuild(), event.getMember(), event.getChannel(), args);
        } catch (CommandNotFoundException | IOException ignored) { }
    }

    private void chatter(RixaGuild rixaGuild, TextChannel channel, String message) {
        ConversationModule conversationModule = (ConversationModule) rixaGuild.getModule("Conversation");
        if (!conversationModule.isEnabled()) return;
        try {
            MessageFactory.create(conversationModule.getChatBotSession().think(message)).selfDestruct(0)
            .queue(channel);
        } catch (Exception ignored) {}
    }
}
