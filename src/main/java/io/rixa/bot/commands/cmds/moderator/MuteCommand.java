package io.rixa.bot.commands.cmds.moderator;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import io.rixa.bot.utils.Utils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class MuteCommand extends Command {

    private RixaPermission rixaPermission;
    public MuteCommand(String command, RixaPermission rixaPermission, String description) {
        super(command, rixaPermission, description);
        this.rixaPermission = rixaPermission;
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        // RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        String argumentString = String.join(" ", args);
        Object[] objArray = DiscordUtils.memberSearchArray(guild, argumentString, false);
        if (objArray.length == 0) {
            MessageFactory.create("Could not find member!").setColor(member.getColor()).queue(channel);
        }
        Member targetMember = (Member) objArray[1];
        String targetMemberName = String.valueOf(objArray[0]);
        if (targetMember == null) {
            MessageFactory.create("Could not find member!").setColor(member.getColor()).queue(channel);
            return;
        }
        argumentString = argumentString.replaceFirst(targetMemberName, "").trim();
        args = argumentString.split(" ");
        String time = args[0].trim();
        argumentString = String.join(" ", args).replaceFirst(time, "");
        long milliseconds = Utils.toMilliSec(time);
        MessageFactory.create("Duration: " + time + "\nReason: " + argumentString + "\nDuration in Milliseconds: " + milliseconds).queue(channel);
    }
}
