package io.rixa.guild.manager;

import io.rixa.guild.RixaGuild;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildMapper implements RowMapper<RixaGuild> {

    @Override
    public RixaGuild mapRow(ResultSet resultSet, int i) throws SQLException {
        RixaGuild guild = new RixaGuild(null);
        guild.load(resultSet);
        // Register guild;
        return guild;
    }
}
