package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.LevelsModule;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class LeaderboardsCommand extends Command {

  public LeaderboardsCommand(String command, RixaPermission rixaPermission, String description,
      CommandType commandType) {
    super(command, rixaPermission, description, commandType);
  }

  @Override
  public void execute(String commandLabel, Guild guild, Member member, TextChannel channel,
      String[] args) {
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
    LevelsModule levelsModule = (LevelsModule) rixaGuild.getModule("Levels");
    int page = 1;
    List<String> leaderboard = getLeaderboard(rixaGuild, page);
    MessageFactory
        .create(leaderboard.isEmpty() ? "No users found!" : (String.join("\n", leaderboard)))
        .setAuthor("Leaderboard: " + guild.getName(), guild.getIconUrl())
        .setColor(member.getColor())
        .queue(channel, message -> {
          message.addReaction("\u2B05").complete();
          message.addReaction("\u27A1").complete();
        }).footer("Page: (" + page + " / " + levelsModule.getObjectPagination().getMaxPage() + ")",
        member.getGuild().getIconUrl());

  }

  private List<String> getLeaderboard(RixaGuild rixaGuild, int page) {
    LevelsModule levelsModule = (LevelsModule) rixaGuild.getModule("Levels");
    List<Object> objects = levelsModule.getObjectPagination().getPage(page);
    List<String> leaderboard = new ArrayList<>();
    objects.forEach(s -> {
      String[] string = String.valueOf(s).split(":");
      User user = rixaGuild.getGuild().getJDA().getUserById(string[0]);
      int exp = Integer.parseInt(string[1]);
      leaderboard.add
          (user.getName() + "#" + user.getDiscriminator() + " (Lvl. " +
              DiscordUtils.getLevelFromExperience(exp) + ")");
    });
    return leaderboard;
  }
}
