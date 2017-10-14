package me.savvy.rixa.modules.economy;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.modules.RixaModule;

public class EconomyModule implements RixaModule {

    @Getter
    @Setter
    private boolean enabled;

    @Override
    public String getName() {
        return "Economy";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }
}
