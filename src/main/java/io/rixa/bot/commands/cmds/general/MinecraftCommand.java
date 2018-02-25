package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.utils.MessageFactory;
import io.rixa.bot.utils.WebUtil;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MinecraftCommand extends Command {

    public MinecraftCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType, List<String>aliases) {
        super(command, rixaPermission, description, commandType, aliases);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        if (args.length == 0) {
            MessageFactory.create("Incorrect Usage! Try " + commandLabel + " {IP}:{PORT}").setColor(member.getColor()).queue(channel);
            return;
        }

        String ipAddress = args[0];

        JSONObject object = get(ipAddress);
        if (object == null || !object.getBoolean("status")) {
            // Not correct
            MessageFactory.create("Server Information not found!").setColor(member.getColor()).queue(channel);
            return;
        }
        JSONObject onlineObject = object.getJSONObject("players");
        MessageFactory.create(
                object.getJSONObject("motds").getString("clean"))
                .setTitle(ipAddress)
                .addField("Version", object.getString("version"), true)
                .addField("Online Players", (onlineObject == null) ? "0/0" : onlineObject.getInt("online") + "/" + onlineObject.getInt("max"), true)
                .addField("Ping", String.valueOf(object.getInt("ping")), true)
                .setImage(String.format("https://use.gameapis.net/mc/query/banner/%s/night,caps", ipAddress))
                .addThumbnail("https://use.gameapis.net/mc/query/icon/" + ipAddress)
                .queue(channel);
    }

    private JSONObject get(String ip) {
        String json;
        try {
            json = WebUtil.getWebPage("https://use.gameapis.net/mc/query/info/" + ip);
        } catch (IOException e) {
            return null;
        }
        return new JSONObject(json);
    }

}
