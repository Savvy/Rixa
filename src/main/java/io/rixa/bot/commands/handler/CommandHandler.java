package io.rixa.bot.commands.handler;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.exceptions.CommandNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    private Map<String, Command> commandMap = new HashMap<>();

    
    private void registerCommand(Command command) {
       if (commandMap.containsKey(command.getCommand())) return;
       commandMap.put(command.getCommand(), command);
    }

    public void registerCommands(Command...commands) {
        for (Command command : commands) {
            registerCommand(command);
        }
    }

    public Command getCommand(String commandName) throws CommandNotFoundException {
        if (commandMap.containsKey(commandName.toLowerCase())) return commandMap.get(commandName.toLowerCase());
        for(Command command: commandMap.values()) {
            if (command.getAliases().contains(commandName)) {
                return command;
            }
        }
        throw new CommandNotFoundException("Could not find command");
    }
}