package io.rixa.bot.utils;

import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageFactory {

    private Message message;

    @Getter
    private EmbedBuilder builder;
    @Getter private int selfDestruct;

    public MessageFactory(String description) {
        this();
        setDescription(description);
    }

    public MessageFactory() {
        this.builder = new EmbedBuilder();
        selfDestruct = 20;
    }

    public MessageFactory setTitle(String title, String url) {
        builder.setTitle(title, url);
        return this;
    }

    public MessageFactory setDescription(String description) {
        builder.setDescription(description);
        return this;
    }

    public MessageFactory setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public MessageFactory setImage(String image) {
        builder.setImage(image);
        return this;
    }

    public MessageFactory setColor(Color color) {
        builder.setColor(color);
        return this;
    }

    public MessageFactory addField(String name, String value, boolean inLine) {
        builder.addField(name, value, inLine);
        return this;
    }

    public MessageFactory addThumbnail(String url) {
        builder.setThumbnail(url);
        return this;
    }

    public MessageFactory setAuthor(String name, String url, String iconURL) {
        builder.setAuthor(name, url, iconURL);
        return this;
    }

    public MessageFactory setAuthor(String name, String iconURL) {
        builder.setAuthor(name, iconURL, iconURL);
        return this;
    }

    public void queue(TextChannel channel) {
        try {
            message = channel.sendMessage(builder.build()).complete(true);
            destroy();
        } catch (PermissionException ex) {
            System.out.println("I do not have permission: " + ex.getPermission().getName() + " on server " + channel.getGuild().getName() + " in channel: " + channel.getName());
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }

    public Message complete(TextChannel channel) {
        try {
            message = channel.sendMessage(builder.build()).complete();
            destroy();
            return message;
        } catch (PermissionException ex) {
            System.out.println("I do not have permission: " + ex.getPermission().getName() + " on server " + channel.getGuild().getName() + " in channel: " + channel.getName());
            return null;
        }
    }

    public void send(User member) {
        member.openPrivateChannel().complete().sendMessage(builder.build()).queue();
        destroy();
    }

    public MessageFactory sendUser(User member) {
        this.message = member.openPrivateChannel().complete().sendMessage(builder.build()).complete();
        destroy();
        return this;
    }

    public MessageFactory addReaction(String reaction) {
        if(message == null) {
            throw new NullPointerException("Message must not be null!");
        }
        message.addReaction(reaction).complete();
        return this;
    }

    public MessageFactory selfDestruct(int time) {
        this.selfDestruct = time;
        return this;
    }

    private void destroy() {
        if (getSelfDestruct() == 0) return;
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            if (message != null) message.delete().queue();
            executor.shutdown();
        }, getSelfDestruct(), TimeUnit.SECONDS);
    }

    public MessageEmbed build() {
        return builder.build();
    }

    public MessageFactory setThumbnail(String thumbnail) {
        builder.setThumbnail(thumbnail);
        return this;
    }

    public MessageFactory footer(String s, String iconURL) {
        builder.setFooter(s, iconURL);
        return this;
    }

    public static MessageFactory create() {
        return new MessageFactory();
    }

    public static MessageFactory create(String s) {
        return new MessageFactory(s);
    }
}