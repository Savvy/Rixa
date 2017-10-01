package me.savvy.rixa.modules.conversations;

import com.google.code.chatterbotapi.*;
import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.RixaModule;
import me.savvy.rixa.utils.DatabaseUtils;

import java.sql.PreparedStatement;

public class ConversationModule implements RixaModule {

    private final RixaGuild rixaGuild;
    private ChatterBotFactory factory;
    private ChatterBotSession chatBotSession;
    private ChatterBot chatBot;
    @Getter
    @Setter
    private boolean enabled;


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
        return enabled;
    }

    @Override
    public void load() {
        try {
            PreparedStatement ps = Rixa.getDatabase().getPreparedStatement("SELECT `conversations` FROM `modules` WHERE `guild_id` = ?");
            ps.setString(1, rixaGuild.getGuild().getId());
            this.enabled = Rixa.getDatabase().getBoolean(ps, "enabled");
            factory = new ChatterBotFactory();
            chatBot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            chatBotSession = chatBot.createSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        DatabaseUtils.update("modules", "conversations", "guild_id", enabled, rixaGuild.getGuild().getId());
    }

    public ChatterBotSession getChatBotSession() {
        return chatBotSession;
    }
}
