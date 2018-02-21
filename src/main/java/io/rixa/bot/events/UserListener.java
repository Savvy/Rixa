package io.rixa.bot.events;

import io.rixa.bot.Rixa;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class UserListener {

  @SubscribeEvent
  public void onJoin(GuildMemberJoinEvent event) {
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
    if (!rixaGuild.getSettings().getJoinMessage().equalsIgnoreCase("default_value")
        && rixaGuild.getSettings().getGreetings() != null) {
      MessageFactory.create(rixaGuild.getSettings().getJoinMessage()
          .replace("%guild%", event.getGuild().getName())
          .replace("%user%", event.getUser().getName())
          .replace("%joinPosition%", String.valueOf(event.getGuild().getMembers().size())))
          .selfDestruct(0).queue(rixaGuild.getSettings().getGreetings());
    }
    if (!rixaGuild.getSettings().isJoinVerification()) {
      if (rixaGuild.getSettings().getDefaultRole() != null) {
        event.getGuild().getController()
            .addRolesToMember(event.getMember(), rixaGuild.getSettings().getDefaultRole()).queue();
      }
      return;
    }
    if (!rixaGuild.getConfirmationUsers().contains(event.getUser().getId())) {
      rixaGuild.getConfirmationUsers().add(event.getUser().getId());
    }
    MessageFactory.create(rixaGuild.getSettings().getJoinPrivateMessage()
        .replace("%guild%", event.getGuild().getName())
        .replace("%user%", event.getUser().getName())
        .replace("%joinPosition%", String.valueOf(event.getGuild().getMembers().size())))
        .selfDestruct(0).send(event.getUser(), success ->
        success.addReaction("\uD83D\uDC4D").queue(s ->
            success.addReaction("\uD83D\uDC4E").queue()));
  } // 👍

  @SubscribeEvent
  public void onAddReactionPM(PrivateMessageReactionAddEvent event) {
    if (event.getUser().isBot()) {
      return;
    }
    String messageId = event.getMessageId();
    Message message = event.getChannel().getMessageById(messageId).complete();
    if (message == null || message.getEmbeds().size() == 0) {
      return;
    }
    if (!event.getReaction().getReactionEmote().getName().contains("\uD83D\uDC4D") &&
        !event.getReaction().getReactionEmote().getName().contains("\uD83D\uDC4E")) {
      return;
    }
    // Add check to see if reaction added is a thumbs up or down
    MessageEmbed messageEmbed = message.getEmbeds().get(0);
    if (messageEmbed.getFooter() == null || messageEmbed.getFooter().getText().isEmpty()) {
      return;
    }
    String guildId = messageEmbed.getFooter().getText();
    Guild guild = null;
    for (JDA jda : Rixa.getInstance().getShardList()) {
      if (jda.getGuildById(guildId) != null) {
        guild = jda.getGuildById(guildId);
      }
    }
    if (guild == null) {
      return;
    }
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
    if (!rixaGuild.getConfirmationUsers().contains(event.getUser().getId())) {
      return;
    }
    rixaGuild.getConfirmationUsers().remove(event.getUser().getId());
    guild.getController().addRolesToMember(
        guild.getMember(event.getUser()), rixaGuild.getSettings().getDefaultRole()).queue();
  }

  @SubscribeEvent
  public void onQuit(GuildMemberLeaveEvent event) {
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
    if (rixaGuild.getConfirmationUsers().contains(event.getUser().getId())) {
      rixaGuild.getConfirmationUsers().remove(event.getUser().getId());
    }
    if (!rixaGuild.getSettings().getJoinMessage().equalsIgnoreCase("default_value") &&
        rixaGuild.getSettings().getFarewell() != null) {
      MessageFactory.create(rixaGuild.getSettings().getQuitMessage()
          .replace("%guild%", event.getGuild().getName())
          .replace("%user%", event.getUser().getName())
          .replace("%joinPosition%", String.valueOf(event.getGuild().getMembers().size())))
          .selfDestruct(0).queue(rixaGuild.getSettings().getFarewell());
    }
  }
}
