package me.savvy.rixa.extras.polls;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 */
public class Poll {
    
    @Getter @Setter
    private String name, description;
    @Getter
    private List<String> options;

    public Poll(String name) {
        this.name = name;
        options = new ArrayList<>();
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
