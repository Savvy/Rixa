package me.savvy.rixa.modules.audio;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 * Created by Timber on 6/5/2017.
 */
public class RixaAudioReceiveHandler implements AudioReceiveHandler {

    private Guild guild;

    public void start(Guild guild, VoiceChannel channel) {
        this.guild = guild;
        AudioManager manager = guild.getAudioManager();
        manager.openAudioConnection(channel);
        manager.setReceivingHandler(this);
    }

    @Override
    public boolean canReceiveCombined() {
        //Lets JDA know that it's OK to send audio to this class
        return true;
    }

    @Override
    public boolean canReceiveUser() {
        //Lets JDA know that it's OK to send audio to this class
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio audio) {
        //This will give you audio from all users, packet by packet.
        //You can do with this whatever you want, pass it onto a sending handler, write it to a file etc
    }

    @Override
    public void handleUserAudio(UserAudio audio) {
        //This will give you audio from a single user, packet by packet
        guild.getTextChannelById("301790750327308290").sendMessage(audio.getUser().getAsMention() + " is talking").queue();
    }
}
