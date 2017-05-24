package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 * Created by Timber on 5/7/2017.
 */
public class HelpCommand implements CommandExec {

    @Command(usage = "%phelp",
    channelType = ChannelType.TEXT,
    description = "Receive information about the server!",
    aliases = "", mainCommand = "help")
    public void execute(GuildMessageReceivedEvent event) {
        try {
            event.getMessage().delete().complete();
        } catch (PermissionException ignored) {}
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String stringBuilder = "\u2753" +
                " **Help**" +
                "\n" +
                "Click the corresponding number for more information about the command menu.";
        embedBuilder.setTitle("Help", "http://rixa.io")
                .setDescription(stringBuilder)
                .addField("1 - General Commands", "Reveals usable commands intended for `everyone`", false)
                .addField("2 - Staff Commands", "Reveals usable commands intended for `staff` use only", false)
                .addField("3 - Music Commands", "Reveals usable commands to configure Rixa for your discord!", false)
                .setColor(event.getMember().getColor());
        Message message = event.getAuthor().openPrivateChannel().complete().sendMessage(embedBuilder.build()).complete();
        new MessageBuilder(event.getMember().getAsMention()
                + ", the help menu has been private messaged to you!").setColor(event.getMember().getColor()).queue(event.getChannel());
        try {
            message.addReaction("\u0031\u20E3").queue();
            message.addReaction("\u0032\u20E3").queue();
            message.addReaction("\u0033\u20E3").queue();
            message.addReaction("\uD83D\uDDD1").queue();
        } catch (ErrorResponseException ignored) {}
    }
}
