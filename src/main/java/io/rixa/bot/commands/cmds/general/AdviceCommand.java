package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import io.rixa.bot.utils.WebUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;

import java.io.IOException;

public class AdviceCommand extends Command {

    public AdviceCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        MessageFactory.create(getAdvice()).setTitle("Advice Request").footer("Requested by: " + member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
                .setColor(member.getColor()).setTimestamp().queue(channel);
    }

    private String getAdvice() {
        String json;
        try {
            json = WebUtil.getWebPage("http://api.adviceslip.com/advice");
        } catch (IOException e) {
            return "Could not find any advice for you.";
        }

        JSONObject obj = new JSONObject(json);
        JSONObject slip = obj.getJSONObject("slip");
        return slip.getString("advice");
    }
}
