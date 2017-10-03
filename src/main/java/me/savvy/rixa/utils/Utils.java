package me.savvy.rixa.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 */
public class Utils {

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static List<Member> memberSearch(Guild guild, String string, boolean bots) {
        List<Member> members = new ArrayList<>();
        String finalString = string.toLowerCase();
        for (Member member : guild.getMembers()) {
            if ((member.getUser().getName().toLowerCase() + "#" + member.getUser().getDiscriminator()).contains(finalString)
                    || (member.getEffectiveName().toLowerCase().contains(finalString))
                    || finalString.contains(member.getUser().getId())
                    || finalString.contains(member.getUser().getName().toLowerCase() + "#" + member.getUser().getDiscriminator())
                    || finalString.contains(member.getEffectiveName())
                    ) {

                if (!bots && member.getUser().isBot()) continue;
                members.add(member);
            }
        }

        return members;
    }

    public static List<Role> roleSearch(Guild guild, String string) {
        List<Role> roles = new ArrayList<>();
        guild.getRoles().forEach(role -> {
            if (role.getName().toLowerCase().contains(string.toLowerCase())
                    || string.contains(role.getId()))
                roles.add(role);
        });
        return roles;
    }
}
