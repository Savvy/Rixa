package me.savvy.rixa.commands.general;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Timber on 5/7/2017.
 */
public class InfoCommand implements CommandExec {

    @Command(
            description = "Receive information about Rixa",
            type = CommandType.USER,
            channelType = ChannelType.TEXT,
            usage = "%pinfo", mainCommand = "info")
    public void execute(GuildMessageReceivedEvent event) {
        EmbedBuilder messageEmbed = new EmbedBuilder();
        String[] messages = event.getMessage().getContent().split(" ");
        if(messages.length == 1) {
            User botOwner = event.getJDA().getUserById("202944101333729280");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            Date date1 = new Date(Rixa.getTimeUp());
            Date date2 = new Date();
            long difference = date2.getTime() - date1.getTime();
            long seconds = difference / 1000;
            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
            long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
            long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
            int guildCount = 0;
            int userCount = 0;
            for (JDA jda : Rixa.getShardsList()) {
                guildCount += jda.getGuilds().size();
                userCount += jda.getUsers().size();
            }
            messageEmbed
                    .setTitle("Rixa Discord Bot", "http://rixa.io/")
                    .setDescription("Rixa is a user-friendly, multi-purpose bot currently in development which is capable of being customized to your Discord server needs. " +
                            "Rixa is complete with a dashboard, user profile, server statistics system, and many more features such as assigning roles on user join, music module, " +
                            "levels, and more. Rixa was created to bring ease and simplicity to managing Discord servers, and has since grown into much more than just a bot used for " +
                            "moderation.")
                    .addField("Created", event.getJDA().getSelfUser().getCreationTime().format(formatter), true)
                    .addField("Bot Uptime ", "Uptime: " + day + " days " + hours + " hours " + minute + " minutes " + second + " seconds.", true)
                    .addField("Total Guilds", String.valueOf(guildCount), true)
                    .addField("Total Users", String.valueOf(userCount), true)
                    .addField("JDA Version", JDAInfo.VERSION, true)
                    .addField("Rixa Developer", botOwner.getName() + "#" + botOwner.getDiscriminator(), true)
                    .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl());
            event.getChannel().sendMessage(messageEmbed.build()).queue();

        } else if(messages.length >= 2) {
            if(event.getMessage().getMentionedUsers().size() != 1) {
                return;
            }

            User user = event.getMessage().getMentionedUsers().get(0);
                Member member = event.getGuild().getMember(user);
                OffsetDateTime time = user.getCreationTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy HH:mm:ss");
                EmbedBuilder eb = new EmbedBuilder();
                if(member.getGame() != null) {
                    eb.setDescription("Playing **" + member.getGame().getName() + "**");
                }
                eb.setAuthor("User Information: " + user.getName(), null, user.getAvatarUrl());
                eb.setColor(member.getColor());
                eb.setThumbnail(user.getAvatarUrl());

            eb.addField("User", user.getAsMention(), true)
                        .addField("ID", user.getId(), true)
                        .addField("Roles", String.valueOf(member.getRoles().size()), true)
                        .addField("Status", member.getOnlineStatus().name(), true)
                        .addField("Mutual Guilds", String.valueOf(user.getMutualGuilds().size()), true);
                if(member.getNickname() != null) {
                    eb.addField("Nickname", member.getNickname(), true);
                }
                eb.addField("Created", time.format(formatter), true)
                        .addField("Joined", member.getJoinDate().format(formatter), true);
                MessageEmbed embed = eb.build();
                event.getChannel().sendMessage(embed).complete();
            }
    }
}
