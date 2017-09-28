package me.savvy.rixa.modules;

import me.savvy.rixa.guild.RixaGuild;

/**
 * Created by Timber on 5/23/2017.
 */
public interface RixaModule {

    String getName();

    String getDescription();

    boolean isEnabled();

    void load(RixaGuild guild);

    void save();
}
