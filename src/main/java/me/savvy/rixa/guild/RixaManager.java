package me.savvy.rixa.guild;

import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timber on 5/23/2017.
 */
public class RixaManager {

    private static Map<String, RixaGuild> guilds = new HashMap<>();

    public static Map<String, RixaGuild> getGuilds() {
        return guilds;
    }

    public static void addGuild(RixaGuild guild) {
        if(check(guild.getGuild())) return;
        guilds.put(guild.getGuild().getId(), guild);
    }

    public static RixaGuild getGuild(Guild guild) {
        if(!check(guild)) {
            addGuild(new RixaGuild(guild));
        }
        return guilds.get(guild.getId());
    }

    public static void removeGuild(RixaGuild guild) {
        if(!check(guild.getGuild())) return;
        guilds.remove(guild.getGuild().getId());
    }

    private static boolean check(Guild guild) {
        return guilds.containsKey(guild.getId());
    }
}
