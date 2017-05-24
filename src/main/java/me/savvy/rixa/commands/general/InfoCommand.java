package me.savvy.rixa.commands.general;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Timber on 5/7/2017.
 */
public class InfoCommand implements CommandExec {

    @Command(aliases = {""},
            description = "Receive information about Rixa",
            type = CommandType.USER,
            channelType = ChannelType.TEXT,
            usage = "%pinfo", mainCommand = "info")
    public void execute(GuildMessageReceivedEvent event) {
        EmbedBuilder messageEmbed = new EmbedBuilder();
        User botOwner = event.getJDA().getUserById("202944101333729280");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        Date date1 = new Date(Rixa.getInstance().getTimeUp());
        Date date2 = new Date();
        long difference = date2.getTime() - date1.getTime();
        long seconds = difference / 1000;
        int day = (int)TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
                messageEmbed
                        .setTitle("Rixa Discord Bot", "http://rixa.io/")
                        .setDescription("Rixa is a user-friendly, multi-purpose bot that is capable of being customized to your discord server needs. " +
                                "Rixa is complete with a dashboard, user profile, server statistics system, and many more features such as assigning " +
                                "roles on user join, music module, levels, and more. Rixa was created to bring ease and simplicity to managing discord" +
                                " servers, it has since then grown into much more than just a bot used for moderation.")
                        .addField("Created", event.getJDA().getSelfUser().getCreationTime().format(formatter), true)
                        .addField("Bot Uptime ", "Online For: " + day + " days " + hours + " hours " + minute + " minutes " + second + " seconds.", true)
                        .addField("Total Guilds", event.getJDA().getGuilds().size() + "", true)
                        .addField("Total Users", event.getJDA().getUsers().size() + "", true)
                        .addField("Rixa Developer", botOwner.getName() + "#" + botOwner.getDiscriminator(), true)
                        .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl());
        event.getChannel().sendMessage(messageEmbed.build()).queue();
    }
}
