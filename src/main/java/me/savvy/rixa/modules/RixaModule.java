package me.savvy.rixa.modules;

/**
 * Created by Timber on 5/23/2017.
 */
public interface RixaModule {

    String getName();

    String getDescription();

    boolean isEnabled();

    void load();

    void setEnabled(boolean b);

    void save();
}
