package me.savvy.rixa.commands.mod;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.Utils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 */
public class DeleteMessagesCommand implements CommandExec {
    @Override
    @Command(mainCommand = "clear",
    aliases = { "delmessages", "dmessages", "delmsg", "deletemessages" },
    description = "Clear messages from chat!",
    channelType = ChannelType.TEXT,
    type = CommandType.MOD,
    usage = "%pclear")
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        if(!rixaGuild.hasPermission(event.getMember(), RixaPermission.CLEAR_CHAT)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        String[] messages = event.getMessage().getContent().split(" ");
        if(messages.length < 2) {
            new MessageBuilder(event.getMember().getAsMention() + ", please enter an amount. Example /delmessages 5").setColor(event.getMember().getColor()).queue(event.getChannel());
            messages = null;
            return;
        }
        if(!Utils.isInt(messages[1])) {
            new MessageBuilder(event.getMember().getAsMention() + ", Amount must be a number. Example /delmessages 3").setColor(event.getMember().getColor()).queue(event.getChannel());
            messages = null;
            return;
        }
        int amount = Integer.parseInt(messages[1]);
        if (amount < 2 || amount > 100) {
            new MessageBuilder(event.getMember().getAsMention() + ", please provide at least 2 or at most 100 messages to be deleted").setColor(event.getMember().getColor()).queue(event.getChannel());
            messages = null;
            return;
        }
        try {
            RestAction<List<Message>> messageList = event.getChannel().getHistory().retrievePast(amount);
            List<Message> messages1 = new ArrayList<Message>();
            messageList.complete().forEach(message -> {
                if(!event.getChannel().getPinnedMessages().complete().contains(message)) {
                    messages1.add(message);
                }
            });
            event.getChannel().deleteMessages(messages1).complete();
            new MessageBuilder(event.getMember().getAsMention() + " has cleared **" + amount + "** messages from the chat history.").setColor(event.getMember().getColor()).queue(event.getChannel());
        } catch (PermissionException ex) {
            try {
                new MessageBuilder(event.getMember().getAsMention() + ", sorry I do not have permission for this!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } catch (PermissionException e) {
                event.getAuthor()
                        .openPrivateChannel().complete()
                        .sendMessage(event.getMember().getAsMention() + ", sorry I do not have permission for this!").queue();
            }
        } catch(IllegalArgumentException ex) {
            new MessageBuilder(event.getMember().getAsMention() + ", sorry I do not have permission for this!").setColor(event.getMember().getColor()).queue(event.getChannel());
        }
    }
}
