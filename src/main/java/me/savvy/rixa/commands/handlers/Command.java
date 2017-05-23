package me.savvy.rixa.commands.handlers;

import net.dv8tion.jda.core.entities.ChannelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Timber on 5/7/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String mainCommand();

    String[] aliases();

    String description() default "";

    String usage() default "";

    CommandType type()  default CommandType.USER;

    boolean showInHelp() default true;

    ChannelType channelType() default ChannelType.PRIVATE;
}
