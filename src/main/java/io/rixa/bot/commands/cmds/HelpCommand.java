package io.rixa.bot.commands.cmds;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class HelpCommand extends Command {

    public HelpCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        try {
            event.getMessage().delete().complete();
        } catch (PermissionException ignored) {}
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String stringBuilder = "\u2753" +
                " **Help**" +
                "\n" +
                "Click the corresponding number for more information about the command menu.";
        embedBuilder.setTitle(String.format("Help: %s", event.getGuild().getId()))
                .setDescription(stringBuilder)
                .addField("1 - General Commands", "Reveals usable commands intended for `everyone`", false)
                .addField("2 - Staff Commands", "Reveals usable commands intended for `staff` use only", false)
                .addField("3 - Music Commands", "Reveals usable commands to configure Rixa for your discord!", false)
                .setColor(event.getMember().getColor());
        Message message = event.getAuthor().openPrivateChannel().complete().sendMessage(embedBuilder.build()).complete();
        MessageFactory.create(event.getMember().getAsMention()
                + ", the help menu has been private messaged to you!").setColor(event.getMember().getColor()).queue(event.getChannel());
        try {
            message.addReaction("\u0031\u20E3").queue();
            message.addReaction("\u0032\u20E3").queue();
            message.addReaction("\u0033\u20E3").queue();
            message.addReaction("\uD83D\uDDD1").queue();
        } catch (ErrorResponseException ignored) {}
    }
}
