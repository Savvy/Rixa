package io.rixa.bot.commands.exceptions;

public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(String message) {
        super(message);
    }
}
