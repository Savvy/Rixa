package me.savvy.rixa.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;

/**
 * Created by Timber on 5/23/2017.
 */
public class MessageBuilder {

    private EmbedBuilder builder;
    public MessageBuilder(String description) {
        this.builder = new EmbedBuilder().setDescription(description);
    }

    public MessageBuilder setTitle(String title, String url) {
        builder.setTitle(title, url);
        return this;
    }

    public MessageBuilder setTitle(String title) {
        setTitle(title, "http://rixa.io/");
        return this;
    }

    public MessageBuilder setColor(Color color) {
        builder.setColor(color);
        return this;
    }

    public EmbedBuilder getBuilder() {
        return builder;
    }

    public void queue(TextChannel channel) {
        channel.sendMessage(builder.build()).queue();
    }

    public void complete(TextChannel channel) {
        channel.sendMessage(builder.build()).complete();
    }

    public MessageEmbed build() {
        return builder.build();
    }
}
