package io.rixa.bot.commands.cmds.moderator;

import com.dumptruckman.taskmin.Task;
import com.dumptruckman.taskmin.TaskManager;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import io.rixa.bot.utils.Utils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class MuteCommand extends Command {

  private RixaPermission rixaPermission;
  private TaskManager taskManager;

  public MuteCommand(String command, RixaPermission rixaPermission, String description) {
    super(command, rixaPermission, description);
    this.rixaPermission = rixaPermission;
    this.taskManager = TaskManager.createBasicTaskManager();
  }

  @Override
  public void execute(String commandLabel, Guild guild, Member member, TextChannel channel,
      String[] args) {
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
    String argumentString = String.join(" ", args);
    Object[] objArray = DiscordUtils.memberSearchArray(guild, argumentString, false);
    if (objArray.length == 0) {
      MessageFactory.create("Could not find member!").setColor(member.getColor()).queue(channel);
      return;
    }
    Member targetMember = (Member) objArray[0];
    String targetMemberName = String.valueOf(objArray[0]);
    if (targetMember == null) {
      MessageFactory.create("Could not find member!").setColor(member.getColor()).queue(channel);
      return;
    }
    argumentString = argumentString.replaceFirst(targetMemberName, "").trim();
    args = argumentString.split(" ");
    if (args[0].length() == 0) {
      // Incorrect Usage
      return;
    }
    String time = args[0].trim();
    argumentString = String.join(" ", args).replaceFirst(time, "");

    if (argumentString.length() > 255) {
      MessageFactory.create(
          "Sorry your `reason` exceeds the maximum character length of 255!")
          .setColor(member.getColor()).queue(channel);
      return;
    }

    long milliseconds = Utils.toMilliSec(time);
    String reason = argumentString;
    guild.getController().addRolesToMember(targetMember, rixaGuild.getSettings().getMuteRole())
        .queue(onSuccess -> MessageFactory.create(String.format(
            "Temporarily muted %s for %s\n Reason: %s",
            this.getUser(targetMember.getUser()),
            this.getTime(milliseconds),
            reason))
            .setColor(member.getColor()).setTimestamp().queue(channel),
            onFailure -> MessageFactory.create(
                "Could not successfully mute user `" + targetMember.getUser().getName() + "#"
                    + targetMember.getUser()
                    .getDiscriminator() + "`. Reason: " + onFailure.getMessage())
                .setColor(member.getColor()).setTimestamp().queue(channel));
    this.taskManager
        .addTask(Task.builder(() ->
            guild.getController()
                .removeRolesFromMember(targetMember, rixaGuild.getSettings().getMuteRole()).queue())
            .executeAt(LocalDateTime.now().plus(milliseconds, ChronoUnit.MILLIS)));
  }

  private String getTime(long milliseconds) {
    long seconds, minutes, hours, days;
    seconds = milliseconds / 1000;
    minutes = seconds / 60;
    seconds = seconds % 60;
    hours = minutes / 60;
    days = hours / 24;
    minutes = minutes % 60;

    return(String.format("%s days, %s hours, %s minutes, %s seconds", days, hours, minutes, seconds));
  }

  private String getUser(User member) {
    return member.getName() + "#" + member.getDiscriminator();
  }
}