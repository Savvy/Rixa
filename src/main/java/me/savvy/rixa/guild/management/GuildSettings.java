package me.savvy.rixa.guild.management;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by Timber on 5/23/2017.
 */
public class GuildSettings {

    private Guild guild;
    private String prefix = ".", defaultRole, joinMessage, quitMessage, joinPrivateMessage;
    private TextChannel joinMessageChannel, quitMessageChannel;

    public GuildSettings(Guild guild) {
        this.guild = guild;
        load();
    }

    private void load() {

    }

    public void unload() {

    }

    public Guild getGuild() {
        return guild;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }

    public String getJoinPrivateMessage() {
        return joinPrivateMessage;
    }

    public void setJoinPrivateMessage(String joinPrivateMessage) {
        this.joinPrivateMessage = joinPrivateMessage;
    }

    public TextChannel getJoinMessageChannel() {
        return joinMessageChannel;
    }

    public void setJoinMessageChannel(TextChannel joinMessageChannel) {
        this.joinMessageChannel = joinMessageChannel;
    }

    public TextChannel getQuitMessageChannel() {
        return quitMessageChannel;
    }

    public void setQuitMessageChannel(TextChannel quitMessageChannel) {
        this.quitMessageChannel = quitMessageChannel;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
