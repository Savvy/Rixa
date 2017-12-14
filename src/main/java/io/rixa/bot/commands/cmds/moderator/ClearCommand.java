package io.rixa.bot.commands.cmds.moderator;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.utils.MessageFactory;
import io.rixa.bot.utils.Utils;
import lombok.Getter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends Command {

    @Getter
    private RixaPermission rixaPermission;
    public ClearCommand(String command, RixaPermission rixaPermission, String description, List<String> aliases) {
        super(command, rixaPermission, description, aliases);
        this.rixaPermission = rixaPermission;
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        if (args == null || args.length == 0 || !Utils.isInteger(args[0])) {
            MessageFactory.create(String.format("Incorrect Usage! Example: `%s%s 10`", rixaGuild.getSettings().getPrefix(),
                    commandLabel)).setColor(member.getColor()).queue(channel);
            return;
        }
        RixaUser user = new RixaUser();
        if (!(user.hasPermission(rixaPermission))) {
            MessageFactory.create("Sorry! You do not have permission for this command!").setColor(member.getColor()).queue(channel);
            return;
        }
        int amount = Integer.parseInt(args[0]);
        if (amount < 1 || amount > 100) {
            MessageFactory.create("Please try a number less than 100 and greater than 1 and :grimacing:").setColor(member.getColor()).queue(channel);
            return;
        }
        int i = deleteMessages(channel, amount);
        MessageFactory.create("Successfully deleted *" + i + "* messages in " + channel.getAsMention())
                .footer("Requested by: " + member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
                .setColor(member.getColor())
                .queue(channel);
    }

    private int deleteMessages(TextChannel channel, int amount) {
        List<Message> messageHistory= channel.getHistory().retrievePast(amount).complete();
        List<Message> pinnedMessages = channel.getPinnedMessages().complete();
        List<Message> newMessages = new ArrayList<>();
        int i = 0;
        messageHistory.forEach(message -> {
            if (!(pinnedMessages.contains(message))) {
                newMessages.add(message);
            }
        });
        // !mute Savvy 10s Hello Savvy!
        try {
            channel.deleteMessages(newMessages).queue();
            i++;
        } catch (PermissionException ex) {
            if (ex.getPermission() == Permission.MESSAGE_MANAGE)
                MessageFactory.create("I do not have permission to clear messages within this channel!").queue(channel);
        } catch (IllegalArgumentException ignored) {
        }
        return i;
    }
}
