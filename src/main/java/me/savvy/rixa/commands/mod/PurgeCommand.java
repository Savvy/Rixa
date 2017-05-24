package me.savvy.rixa.commands.mod;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.RixaManager;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.Utils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 */
public class PurgeCommand implements CommandExec {

    @Override
    @Command(mainCommand = "purge",
            aliases = { "pmessages", "purgemessages", "purgeuser" },
            description = "Remove a users messages!",
            channelType = ChannelType.TEXT,
            type = CommandType.MOD,
            usage = "%ppurge")
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = RixaManager.getGuild(event.getGuild());
        if(!rixaGuild.hasPermission(event.getMember(), RixaPermission.CLEAR_CHAT)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        if(event.getMessage().getMentionedUsers().size() < 1) {
            new MessageBuilder(event.getMember().getAsMention() + ", could not find user. Try /purge <user>").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        String[] message = event.getMessage().getContent().split(" ");
        if(message.length < 2) {
            new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage. Example /purge <user> [amount]").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        int amount = 100;
        for(String s: message) {
            if(Utils.isInt(s)) {
                amount = Integer.parseInt(s);
                break;
            }
        }
        Member memberToDel = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
        deleteMessage(event.getChannel(), event.getMember(), memberToDel, amount);
    }

    private void deleteMessage(TextChannel channel, Member user, Member userToDel, int amount) {
        try {
            RestAction<List<Message>> messageList = channel.getHistory().retrievePast(amount);
            List<Message> messages = messageList.complete();
            int newAmount = messages.size();
            if(userToDel != null) {
                List<Message> newMsgs = new ArrayList<>();
                for(Message message: messages) {
                    if(message.getAuthor().getId().equals(userToDel.getUser().getId())) {
                        newMsgs.add(message);
                    }
                }
                newAmount = newMsgs.size();
                channel.deleteMessages(newMsgs).complete();
            } else {
                channel.deleteMessages(messages).complete();
            }
            new MessageBuilder(user.getAsMention() + " has cleared **" + newAmount + "** messages from the chat history.").setColor(user.getColor()).queue(channel);
        } catch (PermissionException ex) {
            try {
                new MessageBuilder(user.getAsMention() + ", sorry I do not have permission for this!").setColor(user.getColor()).queue(channel);
            } catch (PermissionException e) {
                user.getUser()
                        .openPrivateChannel().complete()
                        .sendMessage(user.getAsMention() + ", sorry I do not have permission for this!").queue();
            }
        } catch (IllegalArgumentException ex) {
            new MessageBuilder(user.getAsMention() + ", sorry I cannot delete messages older than 2 weeks!").setColor(user.getColor()).queue(channel);
        }
    }
}
