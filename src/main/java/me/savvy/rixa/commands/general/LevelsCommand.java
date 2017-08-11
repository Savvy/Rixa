package me.savvy.rixa.commands.general;

import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.user.UserData;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.YoutubeSearch;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.*;

/**
 * Created by savit on 7/11/2017.
 */
public class LevelsCommand implements CommandExec {

    @Override
    @Command(mainCommand = "rank",
            description = "View your levels!",
            channelType = ChannelType.TEXT)
    public void execute(GuildMessageReceivedEvent event) {
        RixaGuild rixaGuild = RixaGuild.getGuild(event.getGuild());
        if(!rixaGuild.getLevelsModule().isEnabled()) {
            new MessageBuilder("Levels are not enabled on this server!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        UserData data = rixaGuild.getLevelsModule().getUserData(event.getAuthor().getId());
//        Map<UserData, Integer> newMap = new HashMap<>();
//        rixaGuild.getLevelsModule().getUserData().forEach((s, userData) -> {
//            newMap.put(userData, userData.getExperience());
//        });
//        sortHashMapByValues(newMap);
        new MessageBuilder()
                .setAuthor(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                .setTitle(event.getAuthor().getName() + "'s level")
                .setColor(event.getMember().getColor())
                .addField("Rank", "0", true)
                .addField("Level", String.valueOf(data.getLevel()), true)
                .addField("Exp Needed",
                        data.getRemainingExperience() + "/" + data.getNeededXP
                                (data.getLevelFromExperience(data.getExperience())).intValue(), false)
                .addField("Total Exp", String.valueOf(data.getExperience()), false)
                .queue(event.getChannel());
    }

    public LinkedHashMap<UserData, Integer> sortHashMapByValues(
            Map<UserData, Integer> passedMap) {
        List<UserData> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        //Collections.sort(mapKeys);


        LinkedHashMap<UserData, Integer> sortedMap =
                new LinkedHashMap<>();

        for (Integer val : mapValues) {
            Iterator<UserData> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                UserData key = keyIt.next();
                Integer comp1 = passedMap.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
