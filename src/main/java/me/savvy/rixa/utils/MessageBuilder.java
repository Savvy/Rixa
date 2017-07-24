package me.savvy.rixa.utils;

import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

/**
 * Created by Timber on 5/23/2017.
 */
public class MessageBuilder {

    private Message message;
    
    @Getter
    private EmbedBuilder builder;
    public MessageBuilder(String description) {
        this.builder = new EmbedBuilder().setDescription(description);
    }

    public MessageBuilder() {
        this.builder = new EmbedBuilder();
    }

    public MessageBuilder setTitle(String title, String url) {
        builder.setTitle(title, url);
        return this;
    }

    public MessageBuilder setTitle(String title) {
        builder.setTitle(title);
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

    public MessageBuilder addThumbnail(String url) {
        builder.setThumbnail(url);
        return this;
    }

    public MessageBuilder setAuthor(String name, String url, String iconURL) {
        builder.setAuthor(name, url, iconURL);
        return this;
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
