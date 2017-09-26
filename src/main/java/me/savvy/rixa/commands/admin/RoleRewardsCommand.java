package me.savvy.rixa.commands.admin;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class RoleRewardsCommand implements CommandExec {

    @Command(
            description = "View Role Rewards.",
            type = CommandType.ADMIN,
            channelType = ChannelType.TEXT,
            usage = "%prolerewards", mainCommand = "rolerewards",
            aliases =  {"rolereward", "rw"})
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        String[] args = event.getMessage().getContent().split(" ");

        // ?rw <add/remove/list> [level] [role]
        switch (args.length) {
            case 4:
                if (!isInt(args[2])) {
                    new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + args[0] + " <add/remove/list> [level] [role].").
                            setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                if (rixaGuild.getLevelsModule().getRoleRewards().containsKey(Integer.parseInt(args[2]))) {
                    new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + args[0] + " <add/remove/list> [level] [role].").
                            setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "add":
                        break;
                    case "remove":
                        break;
                    default:
                        new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + args[0] + " <add/remove/list> [level] [role].").
                                setColor(event.getMember().getColor()).queue(event.getChannel());
                        break;
                }
                break;
            case 2:
                switch(args[1].toLowerCase()) {
                    case "list":
                        Map<Integer, String> rewards = new HashMap<>();
                        rixaGuild.getLevelsModule().getRoleRewards().forEach((integer, s) -> {

                        });
                        break;
                    default:
                        new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + args[0] + " <add/remove/list> [level] [role].").
                                setColor(event.getMember().getColor()).queue(event.getChannel());
                        break;
                }
                break;
            default:
                new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + args[0] + " <add/remove/list> [level] [role].").
                        setColor(event.getMember().getColor()).queue(event.getChannel());
                break;
        }
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
