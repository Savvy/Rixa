package io.rixa.bot.user.manager;

import io.rixa.bot.user.RixaUser;
import lombok.Getter;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static UserManager instance;
   @Getter private Map<String, RixaUser> userMap = new HashMap<>();

    private UserManager() {
        instance = this;
    }

    public RixaUser getUser(User user) {
        if (hasUser(user.getId()))
        return userMap.get(user.getId());
        RixaUser rixaUser = new RixaUser(user);
        addUser(rixaUser);
        return rixaUser;
    }

    public void addUser(RixaUser user) {
        if (hasUser(user.getUser().getId())) return;
        userMap.put(user.getUser().getId(), user);
    }

    public void removeUser(String id) {
        if (!hasUser(id)) return;
        userMap.remove(id);
    }

    public boolean hasUser(String id) { return userMap.containsKey(id); }

    public static UserManager getInstance() {
        return (instance == null) ? new UserManager() : instance;
    }
}
