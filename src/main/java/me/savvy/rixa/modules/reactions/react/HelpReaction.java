package me.savvy.rixa.modules.reactions.react;

import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.reactions.handlers.React;
import me.savvy.rixa.modules.reactions.handlers.ReactHandle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

/**
 * Created by Timber on 5/21/2017.
 */
public class HelpReaction implements React {

    @Override
    @ReactHandle(title = "Help", description = "Help menu for Rixa")
    public void reactionTrigger(MessageReactionAddEvent event) {
        if(event.getChannel().getType() != ChannelType.PRIVATE
                || event.getUser().getId().equalsIgnoreCase(event.getJDA().getSelfUser().getId())) {
            return;
        }
        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
        String title = message.getEmbeds().get(0).getTitle().split(": ")[1];
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getJDA().getGuildById(title));
        String prefix = rixaGuild.getGuildSettings().getPrefix();
        EmbedBuilder embedBuilder;
        try {
            switch(event.getReaction().getEmote().getName()) {
                case "\uD83D\uDDD1":// waste bin
                    if (message.getAuthor().getId().equalsIgnoreCase(event.getJDA().getSelfUser().getId())) {
                        message.delete().complete();
                    }
                    break;
                case "\u0031\u20E3":// one emoji
                    embedBuilder = new EmbedBuilder();
                    String stringBuilder = "\u2753" +
                            " **General Commands Help**" +
                            "\n" +
                            "Click a number below for information about other commands.";
                    embedBuilder.setTitle(String.format("Help: %s", title));
                    embedBuilder.setDescription(stringBuilder);
                    CommandHandler.getCommands().values().stream().filter(cmd -> cmd.getAnnotation().type() == CommandType.USER)
                            .forEach(cmd -> embedBuilder.addField(prefix + cmd.getAnnotation().mainCommand(),
                                    cmd.getAnnotation().description(), false));
                    message.editMessage(embedBuilder.build()).queue();
                    break;
                case "\u0032\u20E3": // two emoji
                    embedBuilder = new EmbedBuilder();
                    stringBuilder = "\u2753" +
                            " **Staff Commands Help**" +
                            "\n" +
                            "Click a number below for information about other commands.";
                    embedBuilder.setTitle(String.format("Help: %s", title));
                    embedBuilder.setDescription(stringBuilder);
                    CommandHandler.getCommands().values().stream().filter(cmd -> cmd.getAnnotation().type() == CommandType.ADMIN
                            || cmd.getAnnotation().type() == CommandType.MOD)
                            .forEach(cmd -> embedBuilder.addField(prefix + cmd.getAnnotation().mainCommand(), cmd.getAnnotation().description(), false));
                    message.editMessage(embedBuilder.build()).queue();
                    break;
                case "\u0033\u20E3": // three emoji
                    embedBuilder = new EmbedBuilder();
                    stringBuilder = "\u2753" +
                            " **Music Commands Help**" +
                            "\n" +
                            "Click a number below for information about other commands.";
                    embedBuilder.setTitle(String.format("Help: %s", title));
                    embedBuilder.setDescription(stringBuilder);
                    embedBuilder.addField(prefix + "music join [name]", "Joins a voice channel that has the provided name", false)
                            .addField(prefix + "music join [id]", "Joins a voice channel based on the provided id.", false)
                            .addField(prefix + "music leave", "Leaves the voice channel that the bot is currently in.", false)
                            .addField(prefix + "music play", "Plays songs from the current queue. Starts playing again if it was previously paused", false)
                            .addField(prefix + "music play [url]", "Adds a new song to the queue and starts playing if it wasn't playing already", false)
                            .addField(prefix + "music playlist", "Adds a playlist to the queue and starts playing if not already playing", false)
                            .addField(prefix + "music pause", "Pauses audio playback", false)
                            .addField(prefix + "music stop", "Completely stops audio playback, skipping the current song.", false)
                            .addField(prefix + "music skip", "Skips the current song, automatically starting the next", false)
                            .addField(prefix + "music nowplaying", "Prints information about the currently playing song (title, current time)", false)
                            .addField(prefix + "music np", "Alias for nowplaying", false)
                            .addField(prefix + "music list", "Lists the songs in the queue", false)
                            .addField(prefix + "music volume [vol]", "Sets the volume of the MusicPlayer [10 - 100]", false)
                            .addField(prefix + "music restart", "Restarts the current song or restarts the previous song if there is no current song playing.", false)
                            .addField(prefix + "music repeat", "Makes the player repeat the currently playing song", false)
                            .addField(prefix + "music reset", "Completely resets the player, fixing all errors and clearing the queue.", false);
                    message.editMessage(embedBuilder.build()).queue();
                    break;
            }
        } catch (ErrorResponseException ignored) {
        }
    }
}
