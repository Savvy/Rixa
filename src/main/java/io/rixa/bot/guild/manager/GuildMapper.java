package io.rixa.bot.guild.manager;

import io.rixa.bot.guild.RixaGuild;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildMapper implements RowMapper<RixaGuild> {

    @Override
    public RixaGuild mapRow(ResultSet resultSet, int i) throws SQLException {
        RixaGuild guild = new RixaGuild(null);
        // Register guild;
        return guild;
    }
}
