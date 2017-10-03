package me.savvy.rixa.commands.admin;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.modules.music.MusicModule;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Timber on 6/6/2017.
 */
public class ConfigCommand implements CommandExec {

    private List<String> config;
    private static ConfigCommand instance;
    public ConfigCommand() {
        instance = this;
        config = Arrays.asList(
                "%pconfig set greetings ; Set channel where greeting messages are announced!",
                "%pconfig set farewell ; Set channel where farewell messages are announced!",
                "%pconfig set prefix <prefix> ; Set Rixa's command prefix!",
                "%pconfig set defaultRole <role> ; Set role to be assigned when a user joins the server!",
                "%pconfig set muteRole <role> ; Set role to be assigned when a user is muted!",
                "%pconfig set musicRole <musicRole> ; Set role required to use the music functions! (Not required)",

                "%pconfig set twitterCKey <key> ; Set Twitter Consumer Key!",
                "%pconfig set twitterCSecret <key> ; Set Twitter Consumer Secret!",
                "%pconfig set twitterAToken <key> ; Set Twitter Access Key!",
                "%pconfig set twitterASecret <key> ; Set Twitter Access Secret!",
                "%config set twitterChannel ; Set the channel for Twitter feed updates!",

                "%pconfig set tjoinMessage <joinMessage> ; Set the greetings message for when a user joins the server!",
                "%pconfig set quitMessage <quitMessage> ; Set the quit message for when a user leaves the server!",
                "%pconfig set joinPm <joinPm> ; Set the message to be private messaged when a user joins!",
                "%pconfig set description <description> ; Set your server description!",
                "%pconfig addPerm <role> <permission> ; Give a role permission to access a command!",
                "%pconfig removePerm <role> <permission> ; Remove a role's permission to access a command!",
                "%pconfig enable <module> ; Enabled a Rixa Module!",
                "%pconfig disable <module> ; Disable a Rixa Module!"
                );
    }
    @Override
    @Command(mainCommand = "config",
            description = "Configure Rixa to your liking!",
            type = CommandType.ADMIN,
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = Guilds.getGuild(event.getGuild());
        if (!rixaGuild.hasPermission(event.getMember(), RixaPermission.ACCESS_CONFIG)) {
            new MessageBuilder(event.getMember().getAsMention() + ", you do not have permission for this command.")
                    .setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        String[] messages = event.getMessage().getContent().split(" ");
        if(messages.length == 2) {
            int page = 0;
            try {
                try {
                    page = Integer.parseInt(messages[1]);
                    sendHelp(event.getMember(), page, rixaGuild.getGuildSettings().getPrefix()).sendUser(event.getAuthor()).addReaction("\u2B05").addReaction("\u27A1");
                    event.getMessage().delete().complete();
                    new MessageBuilder(event.getMember().getAsMention() + ", the configuration menu has been private messaged to you").setColor(event.getMember().getColor()).queue(event.getChannel());
                } catch(NumberFormatException ex) {
                    sendHelp(event.getMember(), page, rixaGuild.getGuildSettings().getPrefix()).sendUser(event.getAuthor()).addReaction("\u2B05").addReaction("\u27A1");
                }
            } catch (IllegalArgumentException ex){
                sendHelp(event.getMember(), page, rixaGuild.getGuildSettings().getPrefix()).sendUser(event.getAuthor()).addReaction("\u2B05").addReaction("\u27A1");
                event.getMessage().delete().complete();
                new MessageBuilder(event.getMember().getAsMention() + ", the configuration menu has been private messaged to you").setColor(event.getMember().getColor()).queue(event.getChannel());
            }
            return;
        }else if (messages.length < 3) {
            sendHelp(event.getMember(), 0, rixaGuild.getGuildSettings().getPrefix()).sendUser(event.getAuthor()).addReaction("\u2B05").addReaction("\u27A1");
            event.getMessage().delete().complete();
            new MessageBuilder(event.getMember().getAsMention() + ", the configuration menu has been private messaged to you").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        String message;
      if (messages[1].equalsIgnoreCase("set")) {
            if (messages[2].equalsIgnoreCase("joinpm")) {
                message = getMessage(messages);
                if(event.getMessage().getMentionedChannels().size() > 0) {
                    for (TextChannel messageChannel : event.getMessage().getMentionedChannels()) {
                        message = message.replace(messageChannel.getAsMention(), "<#" + messageChannel.getId() + ">");
                    }
                }
                rixaGuild.getGuildSettings().setJoinPrivateMessage(message);
                new MessageBuilder("Successfully set Private Join Message to\n" + message.replace("{0}", event.getMember().getUser().getName())
                        .replace("{1}", event.getGuild().getName())).setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("description")) {
                message = getMessage(messages);
                rixaGuild.getGuildSettings().setDescription(message);
                new MessageBuilder("Successfully set Server Description to\n" + message.replace("{0}", event.getMember().getUser().getName())
                        .replace("{1}", event.getGuild().getName())).setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("joinmessage")) {
                 message = getMessage(messages);
                 if(event.getMessage().getMentionedChannels().size() > 0) {
                     for (TextChannel messageChannel : event.getMessage().getMentionedChannels()) {
                         message = message.replace(messageChannel.getAsMention(), "<#" + messageChannel.getId() + ">");
                     }
                 }
                 rixaGuild.getGuildSettings().setJoinMessage(message);
                 new MessageBuilder("Successfully set Join Message to\n" + message.replace("{0}", event.getMember().getUser().getName())
                         .replace("{1}", event.getGuild().getName())).setColor(event.getMember().getColor()).queue(event.getChannel());
             } else if (messages[2].equalsIgnoreCase("quitmessage")) {
                 message = getMessage(messages);
                 if(event.getMessage().getMentionedChannels().size() > 0) {
                     for (TextChannel messageChannel : event.getMessage().getMentionedChannels()) {
                         message = message.replace(messageChannel.getAsMention(), "<#" + messageChannel.getId() + ">");
                     }
                 }
                 rixaGuild.getGuildSettings().setQuitMessage(message);
                 new MessageBuilder("Successfully set Quit Message to\n" + message.replace("{0}", event.getMember().getUser().getName())
                         .replace("{1}", event.getGuild().getName())).setColor(event.getMember().getColor()).queue(event.getChannel());
             } else if (messages[2].equalsIgnoreCase("greetings")) {
                rixaGuild.getGuildSettings().setJoinMessageChannel(event.getChannel());
                new MessageBuilder("Successfully updated Greetings channel!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("farewell")) {
                rixaGuild.getGuildSettings().setQuitMessageChannel(event.getChannel());
                new MessageBuilder("Successfully updated Farewell channel!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } /*else if(messages[2].equalsIgnoreCase("logchannel")) {
               rixaGuild.getGuildSettings().setLogChannel(event.getChannel());
                event.getChannel().sendMessage("Successfully updated `Log Channel`").queue();

                break;
            }*/ else if (messages[2].equalsIgnoreCase("prefix")) {
                String pref = messages[3];
                if (pref.length() > 3) {
                    new MessageBuilder("Command prefix can only be 1 to 3 characters long!").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                rixaGuild.getGuildSettings().setPrefix(pref);
                new MessageBuilder("Successfully updated command prefix!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("defaultRole")) {
                if (event.getMessage().getMentionedRoles().size() < 1) {
                    new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " set defaultRole <role>].\nMake sure to mention the role!")
                            .setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                Role role = event.getMessage().getMentionedRoles().get(0);
                rixaGuild.getGuildSettings().setDefaultRole(role.getId());
                new MessageBuilder("Successfully set default role to " + role.getName() + "!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("muteRole")) {
                if (event.getMessage().getMentionedRoles().size() < 1) {
                    new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " set muteRole <role>].")
                            .setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                Role role = event.getMessage().getMentionedRoles().get(0);
                rixaGuild.getGuildSettings().setMuteRole(role.getId());
                new MessageBuilder("Successfully set mute role to " + role.getName() + "!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("musicRole")) {
                if (event.getMessage().getMentionedRoles().size() < 1) {
                    new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " set musicRole <role>].")
                            .setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                Role role = event.getMessage().getMentionedRoles().get(0);
                ((MusicModule) rixaGuild.getModule("Music")).setMusicRole(role.getId());
                new MessageBuilder("Successfully set music role to " + role.getName() + "!").setColor(event.getMember().getColor()).queue(event.getChannel());
            }
        } else if (messages[1].equalsIgnoreCase("enable")) {
            rixaGuild.getModules().keySet().forEach(moduleName -> {
                if (messages[2].equalsIgnoreCase(moduleName)) {
                    rixaGuild.getModule(moduleName).setEnabled(true);
                    new MessageBuilder("Successfully enabled the `" + moduleName + "` module!").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            });
            if (messages[2].equalsIgnoreCase("joinverification")) {
                Guilds.getGuild(event.getGuild()).getGuildSettings().setJoinVerification(true);
                new MessageBuilder("Successfully enabled Join Verification!").setColor(event.getMember().getColor()).queue(event.getChannel());
            }
        } else if (messages[1].equalsIgnoreCase("disable")) {

            rixaGuild.getModules().keySet().forEach(moduleName -> {
                if (messages[2].equalsIgnoreCase(moduleName)) {
                    rixaGuild.getModule(moduleName).setEnabled(false);
                    new MessageBuilder("Successfully disabled the `" + moduleName + "` module!").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            });
                if (messages[2].equalsIgnoreCase("joinverification")) {
                Guilds.getGuild(event.getGuild()).getGuildSettings().setJoinVerification(false);
                new MessageBuilder("Successfully disabled Join Verification!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("joinmessage")) {
                Guilds.getGuild(event.getGuild()).getGuildSettings().setJoinMessageChannel("default_value");
                    new MessageBuilder("Successfully disabled Join messages!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (messages[2].equalsIgnoreCase("quitmessage")) {
                Guilds.getGuild(event.getGuild()).getGuildSettings().setQuitMessageChannel("default_value");
                    new MessageBuilder("Successfully disabled Quit messages!").setColor(event.getMember().getColor()).queue(event.getChannel());
            }
        } else if (messages[1].equalsIgnoreCase("addperm") || messages[1].equalsIgnoreCase("addpermission") || messages[1].equalsIgnoreCase("aperm")) {
            String permission = "notFound";
            for (String string : messages) {
                for(RixaPermission rixaPermission: RixaPermission.values()) {
                    if (rixaPermission.name().equalsIgnoreCase(string)) {
                        permission = string.toUpperCase();
                    }
                }
            }
            if (permission.equalsIgnoreCase("notFound")) {
                new MessageBuilder("Sorry that permission does not exist!").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }

            if (event.getMessage().getMentionedRoles().size() == 0) {
                new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " addPerm <role> <permission>].")
                        .setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            RixaPermission perm = RixaPermission.valueOf(permission.toUpperCase());
            Role role = event.getMessage().getMentionedRoles().get(0);
            if (rixaGuild.hasPermission(role, perm)) {
                new MessageBuilder("That role already has this permission!").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            rixaGuild.setPermission(role, perm, true);
            new MessageBuilder("Successfully given the role " + role.getName() + " the permission " + perm.name() + "!").setColor(event.getMember().getColor()).queue(event.getChannel());
        } else if (messages[1].equalsIgnoreCase("removeperm")
                || messages[1].equalsIgnoreCase("removepermission")
                || messages[1].equalsIgnoreCase("rperm")
                || messages[1].equalsIgnoreCase("delperm")) {
            String permission = "notFound";
            for (String string : messages) {
                for(RixaPermission rixaPermission: RixaPermission.values()) {
                    if (rixaPermission.name().equalsIgnoreCase(string)) {
                        permission = string.toUpperCase();
                    }
                }
            }
            if (permission.equalsIgnoreCase("notFound")) {
                new MessageBuilder("Sorry that permission does not exist!").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            RixaPermission perm = RixaPermission.valueOf(permission.toUpperCase());
            if (event.getMessage().getMentionedRoles().size() == 0) {
                new MessageBuilder(event.getMember().getAsMention() + ", incorrect usage try [" + messages[0] + " removePerm <role> <permission>].")
                        .setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            Role role = event.getMessage().getMentionedRoles().get(0);
            if (!rixaGuild.hasPermission(role, perm)) {
                new MessageBuilder("That role doesn't have this permission!").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
            rixaGuild.setPermission(role, perm, false);
            new MessageBuilder("Successfully removed the permission " + perm.name() + " from the role " + role.getName() + "!").setColor(event.getMember().getColor()).queue(event.getChannel());
        } else {
            int page = 0;
            sendHelp(event.getMember(), page, rixaGuild.getGuildSettings().getPrefix()).sendUser(event.getAuthor()).addReaction("\u2B05").addReaction("\u27A1");
            event.getMessage().delete().complete();
            new MessageBuilder(event.getMember().getAsMention() + ", the configuration menu has been private messaged to you").setColor(event.getMember().getColor()).queue(event.getChannel());
        }
    }

    public MessageBuilder sendHelp(Member member, int page, String prefix) {
        int sizePerPage = 4;
        int maxPages = config.size() / sizePerPage + (config.size() % sizePerPage > 0 ? 1 : 0);
        if(page < 0) {
            page = 0;
        }
        if(page > maxPages - 2) {
            page = maxPages - 3;
        }
        int from = Math.max(0, page * sizePerPage);
        int to = Math.min(config.size(), (page + 2) * sizePerPage);
        List<String> configList = config.subList(from, to);
        MessageBuilder builder = new MessageBuilder("\u2699" + " **Config**" +
                "\n" +
                "Click the back or forward reactions to switch between pages.");
        configList.forEach(object -> {
            builder.addField(object.split(" ; ")[0].replace("%p", prefix), object.split(" ; ")[1], false);
        });
        builder.footer("Page: (" + page + " / " + (maxPages - 2) + ")", member.getGuild().getIconUrl());
        return builder.setColor(member.getColor()).setTitle(String.format("Config: %s", member.getGuild().getId()));
    }

    private String getMessage(String[] messages) {
        StringBuilder builder = new StringBuilder();
        for(int i = 3; i < messages.length; i++) {
            builder.append(messages[i]).append(" ");
        }
        return builder.toString().trim();
    }

    public static ConfigCommand getInstance() {
        return instance;
    }
}