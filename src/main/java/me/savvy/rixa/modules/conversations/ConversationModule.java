package me.savvy.rixa.modules.conversations;

import com.google.code.chatterbotapi.*;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.RixaModule;

public class ConversationModule implements RixaModule {

    private final RixaGuild rixaGuild;
    private ChatterBotFactory factory;
    private ChatterBotSession chatBotSession;
    private ChatterBot chatBot;

    public ConversationModule(RixaGuild rixaGuild) {
        this.rixaGuild = rixaGuild;
        load();
    }
    @Override
    public String getName() {
        return "ConversationModule";
    }

    @Override
    public String getDescription() {
        return "Conversation API - PandoraBots";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void load() {
        try {
            factory = new ChatterBotFactory();
            chatBot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            chatBotSession = chatBot.createSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {}

    public ChatterBotSession getChatBotSession() {
        return chatBotSession;
    }
}
