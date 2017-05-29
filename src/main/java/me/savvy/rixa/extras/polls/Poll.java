package me.savvy.rixa.extras.polls;

import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 */
public class Poll {

    private String name, description;
    private List<String> options;

    public Poll(String name) {
        this.name = name;
        options = new ArrayList<>();
    }

    public String getTitle() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean addOption(String s) {
        if(options.size() == 10) {
            return false;
        }
        options.add(s);
        return true;
    }

    public String removeOption(int i) {
        if(options.size() <= 10) {
            return options.remove(i);
        }
        return "";
    }

    public EmbedBuilder getBuilder(Color color) {
        MessageBuilder messageBuilder = new MessageBuilder(description).setTitle("Polls");
        for (int i = 0; i < options.size(); i++) {
            messageBuilder.addField("Option " + i, options.get(i), true);
        }
        messageBuilder.setColor(color);
        return messageBuilder.getBuilder();
    }
}
