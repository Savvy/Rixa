package io.rixa.bot.events;

import io.rixa.bot.Rixa;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class UserListener {

    @SubscribeEvent
    public void onJoin(GuildMemberJoinEvent event) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
        if (!rixaGuild.getSettings().getJoinMessage().equalsIgnoreCase("default_value")) {
            MessageFactory.create(rixaGuild.getSettings().getJoinMessage()
                    .replace("%guild%", event.getGuild().getName())
                    .replace("%user%", event.getUser().getName())
                    .replace("%joinPosition%", String.valueOf(event.getGuild().getMembers().size())))
                    .selfDestruct(0).send(event.getUser());
        }
        if (!rixaGuild.getSettings().isJoinVerification()) {
            return;
        }
        if (!rixaGuild.getConfirmationUsers().contains(event.getUser().getId())) {
            rixaGuild.getConfirmationUsers().add(event.getUser().getId());
        }
        MessageFactory.create(rixaGuild.getSettings().getJoinPrivateMessage()
                .replace("%guild%", event.getGuild().getName())
                .replace("%user%", event.getUser().getName())
                .replace("%joinPosition%", String.valueOf(event.getGuild().getMembers().size())))
                .selfDestruct(0).addReaction("thumbsUp").addReaction("thumbsDown").send(event.getUser());
    }

    @SubscribeEvent
    public void onAddReactionPM(PrivateMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        String messageId = event.getMessageId();
        Message message = event.getChannel().getMessageById(messageId).complete();
        if (message == null || message.getEmbeds().size() == 0) return;
        if (!event.getReaction().getReactionEmote().getName().contains("thumbsUp") &&
                !event.getReaction().getReactionEmote().getName().contains("thumbsDown")) {
            System.out.println("Neither reaction added although this was: " + event.getReaction().getReactionEmote().getName());
            return;
        }
        // Add check to see if reaction added is a thumbs up or down
        MessageEmbed messageEmbed = message.getEmbeds().get(0);
        if (messageEmbed.getFooter() == null || messageEmbed.getFooter().getText().isEmpty()) return;
        String guildId = messageEmbed.getFooter().getText();
        Guild guild = null;
        for (JDA jda : Rixa.getInstance().getShardList()) {
            if (jda.getGuildById(guildId) != null) {
                guild = jda.getGuildById(guildId);
            }
        }
        if (guild == null) return;
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        if (!rixaGuild.getConfirmationUsers().contains(event.getUser().getId())) return;
        rixaGuild.getConfirmationUsers().remove(event.getUser().getId());
        guild.getController().addRolesToMember(guild.getMember(event.getUser()), rixaGuild.getSettings().getDefaultRole()).queue();
    }

    @SubscribeEvent
    public void onQuit(GuildMemberLeaveEvent event) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
        if (rixaGuild.getConfirmationUsers().contains(event.getUser().getId())) {
            rixaGuild.getConfirmationUsers().remove(event.getUser().getId());
        }
        if (!rixaGuild.getSettings().getJoinMessage().equalsIgnoreCase("default_value")) {
            MessageFactory.create(rixaGuild.getSettings().getQuitMessage()
                    .replace("%guild%", event.getGuild().getName())
                    .replace("%user%", event.getUser().getName())
                    .replace("%joinPosition%", String.valueOf(event.getGuild().getMembers().size())))
                    .selfDestruct(0).send(event.getUser());
        }
    }
}
