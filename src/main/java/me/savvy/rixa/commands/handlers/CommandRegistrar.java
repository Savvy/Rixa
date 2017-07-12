package me.savvy.rixa.commands.handlers;

import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Created by Timber on 5/7/2017.
 */
public class CommandRegistrar {
    
    @Getter
    private final Command annotation;
    @Getter
    private final Method method;
    @Getter
    private final CommandExec executor;

    CommandRegistrar(Command annotation, Method method, CommandExec executor) {
        this.annotation = annotation;
        this.method = method;
        this.executor = executor;
    }
    
}
