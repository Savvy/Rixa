package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfoCommand extends Command {

    public InfoCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member author, TextChannel channel, String[] args) {
        if(args.length >= 1) {
            Member member = DiscordUtils.memberSearch(guild, String.join(" ", args), false).get(0);
            User user = member.getUser();
            OffsetDateTime time = user.getCreationTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy HH:mm:ss");
            List<String> roles = new ArrayList<>();
            member.getRoles().forEach(role -> roles.add(role.getName()));
            MessageFactory.create("Playing **" + (member.getGame() == null ? "Unknown" : member.getGame().getName()) + "**")
                    .setColor(member.getColor())
                    .setThumbnail(user.getAvatarUrl())
                    .setAuthor("User Information: " + user.getName(), null, user.getAvatarUrl())
                    .addField("User", user.getAsMention(), true)
                    .addField("ID", user.getId(), true)
                    .addField("Roles: "+ member.getRoles().size(), String.join(" **,** " + roles), true)
                    .addField("Status", member.getOnlineStatus().name(), true)
                    .addField("Mutual Guilds", String.valueOf(user.getMutualGuilds().size()), true)
                    .addField("Nickname", member.getNickname() == null ? "None" : member.getNickname(), true)
                    .addField("Created", time.format(formatter), true)
                    .addField("Joined", member.getJoinDate().format(formatter), true)
                    .queue(channel);
            return;
        }
        User botOwner = guild.getJDA().getUserById("202944101333729280");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        Date date1 = new Date(Rixa.getInstance().getTimeUp());
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
                .addField("Created", guild.getJDA().getSelfUser().getCreationTime().format(formatter), true)
                .addField("Bot Uptime ", uptime, true)
                .addField("Total Guilds", String.valueOf(guildCount), true)
                .addField("Total Users", String.valueOf(userCount), true)
                .addField("JDA Version", JDAInfo.VERSION, true)
                .addField("Rixa Developer", botOwner.getName() + "#" + botOwner.getDiscriminator(), true)
                .footer("Requested by " + author.getUser().getName() + "#" + author.getUser().getDiscriminator(), author.getUser().getAvatarUrl())
                .queue(channel);
    }

}
