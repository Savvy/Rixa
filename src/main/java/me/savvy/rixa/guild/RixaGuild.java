package me.savvy.rixa.guild;

import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.guild.management.GuildSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

/**
 * Created by Timber on 5/23/2017.
 */
public class RixaGuild {

    private Guild guild;
    private GuildSettings guildSettings;

    public RixaGuild(Guild guild) {
        this.guild = guild;
        load();
    }

    private void load() {
        if(check()) return;
        setGuildSettings(new GuildSettings(this.guild));
        RixaManager.addGuild(this);
    }

    public GuildSettings getGuildSettings() {
        return (guildSettings == null) ? this.guildSettings = new GuildSettings(getGuild()) : guildSettings;
    }

    public void setGuildSettings(GuildSettings guildSettings) {
            this.guildSettings = guildSettings;
    }

    /**
     * TODO: Check if Guild exists in database if not create new instance;
     */
    public boolean check() {
        return guildSettings == null;
    }

    public Guild getGuild() {
        return guild;
    }

    public boolean hasPermission(Member member, RixaPermission clearChat) {
        return member.getUser().getId().equalsIgnoreCase("202944101333729280");
    }
}
