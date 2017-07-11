package me.savvy.rixa.action;

import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by savit on 6/24/2017.
 */
public class ActionManager {

    private Guild guild;
    private Map<String, Action> actionMap;

    public ActionManager(Guild guild) {
        this.guild = guild;
        actionMap = new HashMap<>();
    }

    public Guild getGuild() {
        return guild;
    }

    public Map<String, Action> getActionMap() {
        return actionMap;
    }

    public Action getAction(String actionName) {
        return actionMap.get(actionName);
    }

    public void addAction(String actionName, Action action) {
        if(hasAction(actionName)) {
            actionMap.remove(actionName);
        }
        actionMap.put(actionName, action);
    }

    public void removeAction(String actionName) {
        if(!(hasAction(actionName))) {
            return;
        }
        actionMap.remove(actionName);
    }

    public boolean hasAction(String actionName) {
        return actionMap.containsKey(actionName);
    }
}
