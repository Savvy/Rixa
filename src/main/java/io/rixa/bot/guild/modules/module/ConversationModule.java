package io.rixa.bot.guild.modules.module;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.data.storage.enums.Statements;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.modules.RixaModule;
import lombok.Getter;
import lombok.Setter;

public class ConversationModule implements RixaModule {

    @Getter private ChatterBotFactory factory;
    @Getter private ChatterBotSession chatBotSession;
    @Getter private ChatterBot chatBot;
    @Getter private String name, description;
    @Getter private RixaGuild guild;
    @Getter @Setter boolean enabled;

    public ConversationModule(String name, String description, RixaGuild guild) {
        this.name = name;
        this.description = description;
        this.enabled = true;
        this.guild = guild;
    }

    @Override
    public void load() {
        setEnabled(DatabaseAdapter.getInstance().get().queryForObject
                (Statements.SELECT_MODULE_STATUS.getStatement("{module_name}", getName()),
                        new Object[]{name}, (resultSet, i) -> resultSet.getBoolean("enabled")));
        reload();
    }

    @Override
    public void save() {
        // Check & Set if enabled;
    }

    @Override
    public void reload() {
        if (!isEnabled()) return;
        try {
            factory = new ChatterBotFactory();
            chatBot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            chatBotSession = chatBot.createSession();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
