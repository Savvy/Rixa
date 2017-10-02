package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.old.RixaColor;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.io.File;

public class ColorCommand implements CommandExec {

    @Override
    @Command(mainCommand = "color",
            description = "View a HEX/RGB Color!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        String message = event.getMessage().getContent();
        message = message.replace(rixaGuild.getGuildSettings().getPrefix() + "color ", "");
        RixaColor rixaColor = RixaColor.getInstance();
        Color color = null;
        if (message.startsWith("#")) {
            try {
                color = Color.decode(message);
            } catch (NumberFormatException ex) {
                new MessageBuilder(message + " is not a number! Example: `" +
                        rixaGuild.getGuildSettings().getPrefix() + "color #212121`").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
        } else {
            String[] args = message.split(" ");
            if (args.length != 3) {
                new MessageBuilder(message + " is not a number! Example: `" +
                        rixaGuild.getGuildSettings().getPrefix() + "color 6 2 9`").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            int r = 0, g = 0, b = 0;
            for(int i = 0; i < args.length; i++) {
                Integer x;
                try {
                    x = Integer.parseInt(args[i]);
                } catch (NumberFormatException ex) {
                    new MessageBuilder(args[i] + " is not a number! Example: `" +
                            rixaGuild.getGuildSettings().getPrefix() + "color 6 2 9`").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                switch (i) {
                    case 0:
                        r = x;
                        break;
                    case 1:
                        g = x;
                        break;
                    case 2:
                        b = x;
                }
            }
            try {
                color = new Color(r, g, b);
            } catch (IllegalArgumentException ex) {
                new MessageBuilder(ex.getMessage()).setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
        }

        File file = rixaColor.coverIMG(color);
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        net.dv8tion.jda.core.MessageBuilder msg = new net.dv8tion.jda.core.MessageBuilder();
        msg.setEmbed(new MessageBuilder("Hex: " + hex + ", RGB: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue()).getBuilder().build());
        event.getChannel().sendFile(file, msg.build()).queue();
    }
}
