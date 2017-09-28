package me.savvy.rixa.modules.economy;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class EconomyData {

    @Getter
    @Setter
    private int amount;
    @Getter
    @Setter
    private Guild guild;
    @Getter
    @Setter
    private User user;


    public EconomyData(User user, Guild guild) {
        setUser(user);
        setGuild(guild);
    }
}
