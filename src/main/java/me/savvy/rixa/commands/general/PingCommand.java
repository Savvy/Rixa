package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.temporal.ChronoUnit;

/**
 * Created by Timber on 5/23/2017.
 */
public class PingCommand implements CommandExec {

    @Override
    @Command(mainCommand = "ping",
            description = "Check your ping!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        new MessageBuilder("Pong!").setColor(event.getMember().getColor()).complete(event.getChannel());
        /*event.getChannel().sendMessage("Pong").queue(m -> {
            if(m!=null) {
                m.editMessage("Pong: " + event.getMessage().getCreationTime().until(m.getEditedTime(), ChronoUnit.MILLIS) + "ms").complete();

            }
        });*/
        /*RixaAudioReceiveHandler handle = new RixaAudioReceiveHandler();
        handle.start(event.getGuild(), event.getMember().getVoiceState().getChannel());*/
    }
}
