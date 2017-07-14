package me.savvy.rixa.events;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.List;

/**
 * Created by savit on 7/14/2017.
 */
public class VoiceChannel {

    @SubscribeEvent
    public void handle(GuildVoiceLeaveEvent event) {
        List<Member> members = event.getChannelLeft().getMembers();
        if (members.size() == 1 && members.get(0).getUser().getId().equalsIgnoreCase
                (event.getJDA().getSelfUser().getId())) {
            event.getGuild().getAudioManager().setSendingHandler(null);
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}
