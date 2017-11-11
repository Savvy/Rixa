package io.rixa.bot.guild.manager;

import io.rixa.bot.guild.RixaGuild;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class GuildMapper implements RowMapper<IGuild> {

    @Override
    public IGuild mapRow(ResultSet resultSet, int i) throws SQLException {
        IGuild guild = new RixaGuild(null);
        guild.setDescription(resultSet.getString("description"));
        List<String> keywords = Arrays.asList((String[])resultSet.getArray("keywords").getArray());
        guild.setKeywords(keywords);
        // Register guild;
        return guild;
    }
}
