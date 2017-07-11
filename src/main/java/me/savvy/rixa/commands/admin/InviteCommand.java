package me.savvy.rixa.commands.admin;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.InviteAction;

/**
 * Created by savit on 7/7/2017.
 */
public class InviteCommand implements CommandExec {

    @Command(
            description = "Receive an invite from a server",
            type = CommandType.USER,
            channelType = ChannelType.TEXT,
            usage = "%pinvite", mainCommand = "invite")
    public void execute(GuildMessageReceivedEvent event) {
        User owner = event.getGuild().getOwner().getUser();
        if(!event.getAuthor().getId().equalsIgnoreCase(owner.getId())) {
            return;
        }
        TextChannel channel = event.getJDA().getGuildById(event.getMessage().getContent().split(" ")[1]).getTextChannels().get(0);
        InviteAction inviteAction = channel.createInvite();
       owner.openPrivateChannel().complete().sendMessage(
               "http://discord.gg/" + inviteAction.setMaxUses(1).complete().getCode()).queue();
    }
}
