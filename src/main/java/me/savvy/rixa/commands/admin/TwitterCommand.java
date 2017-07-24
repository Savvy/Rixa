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
 * Created by savit on 7/14/2017.
 */
public class TwitterCommand implements CommandExec {

    @Command(
            description = "Configure twitter module.",
            type = CommandType.ADMIN,
            channelType = ChannelType.TEXT,
            usage = "%ptwitter", mainCommand = "twitter")
    public void execute(GuildMessageReceivedEvent event) {

    }
}