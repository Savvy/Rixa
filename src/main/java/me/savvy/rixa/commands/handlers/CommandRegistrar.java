package me.savvy.rixa.commands.handlers;

import java.lang.reflect.Method;

/**
 * Created by Timber on 5/7/2017.
 */
public class CommandRegistrar {

    private final Command annotation;
    private final Method method;
    private final CommandExec executor;

    CommandRegistrar(Command annotation, Method method, CommandExec executor) {
        this.annotation = annotation;
        this.method = method;
        this.executor = executor;
    }

    public Command getCommandAnnotation() {
        return annotation;
    }

    public Method getMethod() {
        return method;
    }

    public CommandExec getExecutor() {
        return executor;
    }
}
