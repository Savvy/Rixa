package io.rixa.bot.commands.cmds;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class InfoCommand extends Command {

    public InfoCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        System.out.println("INFO COMMAND TRIGGERED");
        String[] messages = event.getMessage().getContent().split(" ");
        System.out.println(messages.length);
        if(messages.length >= 2) {
            Member member = DiscordUtils.memberSearch(event.getGuild(), event.getMessage().getContent(), false).get(0);
            User user = member.getUser();
            OffsetDateTime time = user.getCreationTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy HH:mm:ss");
            MessageFactory.create("Playing **" + (member.getGame() == null ? "Unknown" : member.getGame().getName()) + "**")
                    .setColor(member.getColor())
                    .setThumbnail(user.getAvatarUrl())
                    .setAuthor("User Information: " + user.getName(), null, user.getAvatarUrl())
                    .addField("User", user.getAsMention(), true)
                    .addField("ID", user.getId(), true)
                    .addField("Roles", String.valueOf(member.getRoles().size()), true)
                    .addField("Status", member.getOnlineStatus().name(), true)
                    .addField("Mutual Guilds", String.valueOf(user.getMutualGuilds().size()), true)
                    .addField("Nickname", member.getNickname() == null ? "None" : member.getNickname(), true)
                    .addField("Created", time.format(formatter), true)
                    .addField("Joined", member.getJoinDate().format(formatter), true)
                    .queue(event.getChannel());
            return;
        }
        User botOwner = event.getJDA().getUserById("202944101333729280");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        Date date1 = new Date(Rixa.getTimeUp());
        long difference = new Date().getTime() - date1.getTime();
        long seconds = difference / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        int guildCount = 0;
        int userCount = 0;
        for (JDA jda : Rixa.getInstance().getShardList()) {
            guildCount += jda.getGuilds().size();
            userCount += jda.getUsers().size();
        }
        String uptime = String.format("Uptime: %d days %d hours %d minutes %d seconds", day, hours, minute, second);
        MessageFactory.create("Rixa is a user-friendly, multi-purpose bot currently in development which is capable of being customized to your Discord server needs. " +
                "Rixa is complete with a dashboard, user profile, server statistics system, and many more features such as assigning roles on user join, music module, " +
                "levels, and more. Rixa was created to bring ease and simplicity to managing Discord servers, and has since grown into much more than just a bot used for " +
                "moderation.")
                .setTitle("Rixa Discord Bot", "http://rixa.io/")
                .addField("Created", event.getJDA().getSelfUser().getCreationTime().format(formatter), true)
                .addField("Bot Uptime ", uptime, true)
                .addField("Total Guilds", String.valueOf(guildCount), true)
                .addField("Total Users", String.valueOf(userCount), true)
                .addField("JDA Version", JDAInfo.VERSION, true)
                .addField("Rixa Developer", botOwner.getName() + "#" + botOwner.getDiscriminator(), true)
                .footer("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                .queue(event.getChannel());
    }
}
