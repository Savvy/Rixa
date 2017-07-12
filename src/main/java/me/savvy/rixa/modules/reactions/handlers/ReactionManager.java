package me.savvy.rixa.modules.reactions.handlers;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timber on 5/7/2017.
 */
public class ReactionManager {
    
    @Getter
    private static final Map<String, ReactRegistrar> reactions = new HashMap<>();

    public static void registerReaction(React react) {
        for (Method method : react.getClass().getMethods()) {
            ReactHandle annotation = method.getAnnotation(ReactHandle.class);
            if (annotation == null) continue;
            ReactRegistrar ant = new ReactRegistrar(annotation, method, react);
            reactions.put(annotation.title(), ant);
        }
    }

}