package me.savvy.rixa.action;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by savit on 6/24/2017.
 */
public class ActionManager {
    
    @Getter
    private Guild guild;
    @Getter
    private Map<String, Action> actionMap;

    public ActionManager(Guild guild) {
        this.guild = guild;
        actionMap = new HashMap<>();
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
