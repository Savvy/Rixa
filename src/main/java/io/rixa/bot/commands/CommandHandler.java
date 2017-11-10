package io.rixa.bot.commands;

import io.rixa.bot.commands.exceptions.CommandNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    private Map<String, Command> commandMap = new HashMap<>();

    public void registerCommand(Command command) {
       if (commandMap.containsKey(command.getCommand())) return;
       commandMap.put(command.getCommand(), command);
    }

    public Command getCommand(String commandName) throws CommandNotFoundException {
        for(Command command: commandMap.values()) {
            if (command.getAliases().contains(commandName)) {
                return command;
            }
        }
        throw new CommandNotFoundException("Could not find command");
    }
}