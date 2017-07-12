package me.savvy.rixa.modules.reactions.handlers;

import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Created by Timber on 5/7/2017.
 */
public class ReactRegistrar {
    
    @Getter
    private final ReactHandle annotation;
    @Getter
    private final Method method;
    @Getter
    private final React executor;

    ReactRegistrar(ReactHandle annotation, Method method, React executor) {
        this.annotation = annotation;
        this.method = method;
        this.executor = executor;
    }
}
