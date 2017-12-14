package io.rixa.bot.guild.mapper;

import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.manager.IGuild;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuildMapper implements RowMapper<IGuild> {

    @Override
    public IGuild mapRow(ResultSet resultSet, int i) throws SQLException {
        IGuild guild = GuildManager.getInstance().getGuild(resultSet.getString("guild_id"));
        /*System.out.println("Keywords: " + resultSet.getArray("keywords"));
        System.out.println("Keywords 2: " + resultSet.getArray("keywords").getArray());*/
        String description = resultSet.getString("description");
        String keyWords = resultSet.getString("keywords");
        List<String> keywords = (!keyWords.contains(",") ||
                keyWords.equalsIgnoreCase("No keywords found")) ? Collections.singletonList(keyWords) :
                Arrays.asList(keyWords.split(":"));
        guild.setDescription(description);
        guild.setKeywords(keywords);
        return guild;
    }
}
