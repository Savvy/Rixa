package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class HelpCommand extends Command {

    public HelpCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String stringBuilder = "\u2753" +
                " **Help**" +
                "\n" +
                "Click the corresponding number for more information about the command menu.";
        embedBuilder.setTitle(String.format("Help: %s", guild.getId()))
                .setDescription(stringBuilder)
                .addField("1 - General Commands", "Reveals usable commands intended for `everyone`", false)
                .addField("2 - Staff Commands", "Reveals usable commands intended for `staff` use only", false)
                .addField("3 - Music Commands", "Reveals usable commands to configure Rixa for your discord!", false)
                .setColor(member.getColor());
        Message message = member.getUser().openPrivateChannel().complete().sendMessage(embedBuilder.build()).complete();
        MessageFactory.create(member.getAsMention()
                + ", the help menu has been private messaged to you!").setColor(member.getColor()).queue(channel);
        try {
            message.addReaction("\u0031\u20E3").queue();
            message.addReaction("\u0032\u20E3").queue();
            message.addReaction("\u0033\u20E3").queue();
            message.addReaction("\uD83D\uDDD1").queue();
        } catch (ErrorResponseException ignored) {}
    }
}
