package me.savvy.rixa.commands.handlers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timber on 5/7/2017.
 */
public class CommandHandler {

    private static final Map<String, CommandRegistrar> commands = new HashMap<>();

    public static void registerCommand(CommandExec command) {
        for (Method method : command.getClass().getMethods()) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null) continue;
            CommandRegistrar cmd = new CommandRegistrar(annotation, method, command);
            commands.put(annotation.mainCommand(), cmd);
        }
    }

    public static boolean hasCommand(String s) {
        if(getCommands().containsKey(s)) {
            return true;
        } else {
            for (CommandRegistrar commandRegistrar : getCommands().values()) {
                if (commandRegistrar.getAnnotation().mainCommand().equalsIgnoreCase(s)) {
                    return true;
                }
                for (String string : commandRegistrar.getAnnotation().aliases()) {
                    if (string.equalsIgnoreCase(s)) return true;
                }
            }
            return false;
        }
    }

    public static CommandRegistrar get(String s) {
        if(getCommands().containsKey(s)) {
            return getCommands().get(s);
        } else {
            for (CommandRegistrar commandRegistrar : getCommands().values()) {
                for (String string : commandRegistrar.getAnnotation().aliases()) {
                    if (string.equalsIgnoreCase(s)) return commandRegistrar;
                }
            }
            return null;
        }
    }

    public static Map<String, CommandRegistrar> getCommands() {
        return commands;
    }
}