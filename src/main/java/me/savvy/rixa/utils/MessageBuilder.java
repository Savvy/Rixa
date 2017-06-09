package me.savvy.rixa.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

/**
 * Created by Timber on 5/23/2017.
 */
public class MessageBuilder {

    private Message message;

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

    public MessageBuilder addField(String name, String value, boolean inLine) {
        builder.addField(name, value, inLine);
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

    public void send(User member) {
        member.openPrivateChannel().complete().sendMessage(builder.build()).queue();
    }

    public MessageBuilder sendUser(User member) {
        this.message = member.openPrivateChannel().complete().sendMessage(builder.build()).complete();
        return this;
    }

    public MessageBuilder addReaction(String reaction) {
        if(message == null) {
            throw new NullPointerException("Message must not be null!");
        }
        message.addReaction(reaction).complete();
        return this;
    }

    public MessageEmbed build() {
        return builder.build();
    }

    public MessageBuilder footer(String s, String iconURL) {
        builder.setFooter(s, iconURL);
        return this;
    }
}
