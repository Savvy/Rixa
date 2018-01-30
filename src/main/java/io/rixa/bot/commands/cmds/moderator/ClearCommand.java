package io.rixa.bot.commands.cmds.moderator;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import io.rixa.bot.utils.Utils;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.ErrorResponse;

public class ClearCommand extends Command {

  @Getter
  private RixaPermission rixaPermission;

  public ClearCommand(String command, RixaPermission rixaPermission, String description,
      List<String> aliases) {
    super(command, rixaPermission, description, aliases);
    this.rixaPermission = rixaPermission;
  }

  @Override
  public void execute(String commandLabel, Guild guild, Member member, TextChannel channel,
      String[] args) {
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
    if (args == null || args.length == 0 || !Utils.isInteger(args[0])) {
      MessageFactory.create(
          String.format("Incorrect Usage! Example: `%s%s 10`", rixaGuild.getSettings().getPrefix(),
              commandLabel)).setColor(member.getColor()).queue(channel);
      return;
    }
    int amount = Integer.parseInt(args[0]);
    if (amount < 1 || amount > 100) {
      MessageFactory.create("Please try a number less than 100 and greater than 1 and :grimacing:")
          .setColor(member.getColor()).queue(channel);
      return;
    }
    deleteMessages(channel, amount, member);
  }

  private void deleteMessages(TextChannel channel, int amount, Member member) {
    channel.getHistory().retrievePast(amount).queue(success -> {
      List<Message> pinnedMessages = channel.getPinnedMessages().complete();
      List<Message> newMessages = success.stream()
          .filter(message -> !pinnedMessages.contains(message) && canDelete(message))
          .collect(Collectors.toList());
      try {
        channel.deleteMessages(newMessages).queue(onSuccess -> {
          MessageFactory.create(
              ((newMessages.isEmpty()) ? "Could not find any messages to delete"
                  : "Successfully deleted *" + newMessages.size() + "* messages in " + channel.getAsMention()))
              .footer("Requested by: " + member.getEffectiveName(),
                  member.getUser().getEffectiveAvatarUrl())
              .setColor(member.getColor())
              .queue(channel);
        });
      } catch (PermissionException ex) {
        if (ex.getPermission() == Permission.MESSAGE_MANAGE) {
          MessageFactory.create("I do not have permission to clear messages within this channel!")
              .queue(channel);
        }
      } catch (ErrorResponseException ex) {
        if (ex.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
          Rixa.getInstance().getLogger().info("Ignored message during clear.");
        }
      }
    }, failure -> MessageFactory
        .create("I could not retrieve message history! Please forgive me for this. :(")
        .setColor(member.getColor()).queue(channel));
  }

  private boolean canDelete(Message message) {
    return message.getCreationTime().isAfter(OffsetDateTime.now().minusWeeks(2)
        .plusMinutes(2)); // Pulled from Fredboat
  }
}
