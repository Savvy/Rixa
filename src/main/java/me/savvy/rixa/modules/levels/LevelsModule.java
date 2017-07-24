package me.savvy.rixa.modules.levels;

import lombok.Getter;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.user.UserData;
import me.savvy.rixa.modules.RixaModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timber on 5/23/2017.
 */
public class LevelsModule implements RixaModule {

    @Getter private final RixaGuild rixaGuild;
    @Getter private Map<String, UserData> userData = new HashMap<>();
    @Getter private boolean enabled;

    public LevelsModule(RixaGuild rixaGuild) {
        this.rixaGuild = rixaGuild;
        enabled = true;
    }

    @Override
    public String getName() {
        return "Levels";
    }

    @Override
    public String getDescription() {
        return "Rixa levels module.";
    }

    public void registerUser(UserData userData) {
        if (getUserData().containsKey(userData.getUser().getId())) {
            return;
        }
        getUserData().put(userData.getUser().getId(), userData);
    }

    public UserData getUserData(String key) {
        checkUser(key);
        return getUserData().get(key);
    }

    private void checkUser(String key) {
        if (getUserData().containsKey(key)) {
            return;
        }
        new UserData
                (getRixaGuild().getGuild().getJDA().getUserById(key),
                        getRixaGuild().getGuild());
    }
}
