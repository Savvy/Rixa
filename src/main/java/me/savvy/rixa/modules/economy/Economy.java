package me.savvy.rixa.modules.economy;

import lombok.Getter;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.RixaModule;
import me.savvy.rixa.utils.DatabaseUtils;

import java.util.HashMap;
import java.util.Map;

public class Economy implements RixaModule {

    @Getter
    private RixaGuild rixaGuild;
    @Getter
    private Map<String, EconomyData> userData = new HashMap<>();

    private boolean enabled;

    @Override
    public String getName() {
        return "Economy";
    }

    @Override
    public String getDescription() {
        return "Rixa Economy Module";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void load(RixaGuild rixaGuild) {
        this.rixaGuild = rixaGuild;
    }

    @Override
    public void save() {

    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        DatabaseUtils.update("modules", "levels", "guild_id", enabled, rixaGuild.getGuild().getId());
    }
}