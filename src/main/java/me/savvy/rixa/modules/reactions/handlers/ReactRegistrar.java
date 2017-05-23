package me.savvy.rixa.modules.reactions.handlers;

import java.lang.reflect.Method;

/**
 * Created by Timber on 5/7/2017.
 */
public class ReactRegistrar {

    private final ReactHandle annotation;
    private final Method method;
    private final React executor;

    ReactRegistrar(ReactHandle annotation, Method method, React executor) {
        this.annotation = annotation;
        this.method = method;
        this.executor = executor;
    }

    public ReactHandle getReactAnnotation() {
        return annotation;
    }

    public Method getMethod() {
        return method;
    }

    public React getExecutor() {
        return executor;
    }
}
