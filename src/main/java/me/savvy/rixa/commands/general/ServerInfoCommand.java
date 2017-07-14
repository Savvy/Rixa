package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.guild.RixaGuild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;

/**
 * Created by Timber on 5/7/2017.
 */
public class ServerInfoCommand implements CommandExec {

    @Command(usage = "%pserverinfo",
    channelType = ChannelType.TEXT,
    description = "Receive information about the server!",
    aliases = "sinfo", mainCommand = "serverinfo")
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        EmbedBuilder messageEmbed = new EmbedBuilder();
        User owner = event.getGuild().getOwner().getUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        messageEmbed
                .setTitle(event.getGuild().getName(), "http://rixa.io/servers/" + event.getGuild().getId())
                .setDescription(rixaGuild.getGuildSettings().getDescription())
                .addField("Created", event.getGuild().getCreationTime().format(formatter), true)
                .addField("Region", event.getGuild().getRegion().toString(), true)
                .addField("Users", String.valueOf(event.getGuild().getMembers().size()), true)
                .addField("Text Channels", String.valueOf(event.getGuild().getTextChannels().size()), true)
                .addField("Voice Channels", String.valueOf(event.getGuild().getVoiceChannels().size()), true)
                .addField("Roles", String.valueOf(event.getGuild().getRoles().size()), true)
                .addField("Owner", owner.getName() + "#" + owner.getDiscriminator(), true)
                .addField("Enlisted", String.valueOf(rixaGuild.getGuildSettings().isEnlisted()), true)
                .setThumbnail(event.getGuild().getIconUrl())
                .setFooter("ServerID: " + event.getGuild().getId(), event.getGuild().getIconUrl());
        event.getChannel().sendMessage(messageEmbed.build()).queue();
    }
}
