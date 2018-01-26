package io.rixa.bot.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiscordUtils {
    
    public static List<Member> memberSearch(Guild guild, String string, boolean bots) {
        List<Member> members = new ArrayList<>();
        String finalString = string.toLowerCase();
        for (Member member : guild.getMembers()) {
            if ((member.getUser().getName().toLowerCase() + "#" + member.getUser().getDiscriminator()).contains(finalString)
                    || (member.getEffectiveName().toLowerCase().contains(finalString))
                    || finalString.contains(member.getUser().getId())
                    || finalString.contains(member.getUser().getName().toLowerCase() + "#" + member.getUser().getDiscriminator())
                    || finalString.contains(member.getEffectiveName().toLowerCase())
                    || finalString.contains(member.getUser().getName().toLowerCase())
                    || finalString.equalsIgnoreCase(member.getEffectiveName().toLowerCase())
                    || finalString.equalsIgnoreCase(member.getUser().getName().toLowerCase())) {

                if (!bots && member.getUser().isBot()) continue;
                members.add(member);
            }
        }
        return members;
    }

    public static Object[] memberSearchArray(Guild guild, String string, boolean bots) {
        Object[] array = new Object[2];
        // First item is string, second is member
        String finalString = string.toLowerCase().trim();
        for (Member member : guild.getMembers()) {
            //String nameDescrim = member.getUser().getName().toLowerCase() + "#" + member.getUser().getDiscriminator().toLowerCase();
            if (finalString.contains(member.getUser().getName().toLowerCase() + "#" + member.getUser().getDiscriminator())) {
                array[0] = member.getEffectiveName();
                array[1] = member;
                break;
            } else if (finalString.contains(member.getUser().getId())) {
                array[0] = member.getEffectiveName();
                array[1] = member;
                break;
            }else  if (finalString.contains(member.getEffectiveName().toLowerCase())) {
                array[0] = member.getEffectiveName();
                array[1] = member;
                break;
            } else if (finalString.contains(member.getUser().getName().toLowerCase())) {
                array[0] = member.getEffectiveName();
                array[1] = member;
                break;
            }
        }
        return array;
    }

    public static Role searchFirstRole(Guild guild, String s) {
        return roleSearch(guild, s).get(0);
    }

    public static List<Role> roleSearch(Guild guild, String string) {
        List<Role> roles = new ArrayList<>();
        guild.getRoles().forEach(role -> {
            if (role.getName().toLowerCase().contains(string.toLowerCase())
                    || string.contains(role.getId())
                    || string.toLowerCase().contains(role.getName().toLowerCase()))
                roles.add(role);
        });
        return roles;
    }

    public static Role getMentionedRole(Guild guild, String string) {
        Role mentionedRole = null;
        for (Role role : guild.getRoles()) {
            if (string.contains(role.getAsMention()) || string.contains("@" + role.getName())) {
                mentionedRole = role;
                break;
            }
        }
        return mentionedRole;
    }

    public static VoiceChannel voiceChannelSearch(Guild guild, String string) {
        List<VoiceChannel> voiceChannels = guild.getVoiceChannelsByName(string, true);
        if (!voiceChannels.isEmpty()) {
            return voiceChannels.get(0);
        }
        Optional<VoiceChannel> optional = guild.getVoiceChannels().stream().sorted().filter(voiceChannel -> voiceChannel.getId().equalsIgnoreCase(string) ||
                voiceChannel.getName().equalsIgnoreCase(string) ||
                voiceChannel.getName().contains(string)).findFirst();
        return optional.orElse(null);
    }



    public static int getLevelFromExperience(int xp) {
        int level = 0;
        while (xp >= getNeededXP(level)) {
            xp -= getNeededXP(level);
            level++;
        }
        return level;
    }

    public static Double getNeededXP(double n) {
        if (n < 0) return 0.0;
        return (6 * Math.pow(n, 3) + 119 * n + 100);
    }

    public static int getRemainingExperience(int xp) {
        int level = getLevelFromExperience(xp);

        for (int i = 0; i < level; i++) {
            xp -= getNeededXP(i);
        }
        return xp;
    }
}
