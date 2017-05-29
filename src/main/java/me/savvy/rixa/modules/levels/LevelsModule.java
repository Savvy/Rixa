package me.savvy.rixa.modules.levels;

import me.savvy.rixa.modules.RixaModule;

/**
 * Created by Timber on 5/23/2017.
 */
public class LevelsModule implements RixaModule {
    @Override
    public String getName() {
        return "Levels";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
