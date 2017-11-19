package io.rixa.bot.commands.cmds;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;

public class ServerInfoCommand extends Command {

    public ServerInfoCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        User owner = event.getGuild().getOwner().getUser();
        MessageFactory
                .create(rixaGuild.getDescription())
                .setTitle(event.getGuild().getName(), String.format("http://rixa.io/servers/%s", event.getGuild().getId()))
                .addField("Created", event.getGuild().getCreationTime().format(formatter), true)
                .addField("Region", event.getGuild().getRegion().toString(), true)
                .addField("Users", String.valueOf(event.getGuild().getMembers().size()), true)
                .addField("Channel Categories", String.valueOf(event.getGuild().getCategories().size()), true)
                .addField("Text Channels", String.valueOf(event.getGuild().getTextChannels().size()), true)
                .addField("Voice Channels", String.valueOf(event.getGuild().getVoiceChannels().size()), true)
                .addField("Verification Level", event.getGuild().getVerificationLevel().toString(), true)
                .addField("Roles", String.valueOf(event.getGuild().getRoles().size()), true)
                .addField("Owner", owner.getName() + "#" + owner.getDiscriminator(), true)
                .addField("Enlisted", String.valueOf(true), true)
                .setThumbnail(event.getGuild().getIconUrl())
                .footer("Server Id: " + event.getGuild().getId(), event.getGuild().getIconUrl())
                .queue(event.getChannel());
    }
}
