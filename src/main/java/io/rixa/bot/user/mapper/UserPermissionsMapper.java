package io.rixa.bot.user.mapper;

import io.rixa.bot.commands.perms.RixaPermission;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPermissionsMapper implements ResultSetExtractor<Map<String, List<RixaPermission>>>{
    @Override
    public Map<String, List<RixaPermission>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<String, List<RixaPermission>> permissionsMap = new HashMap<>();
        List<RixaPermission> permissions = new ArrayList<>();
        while (resultSet.next()) {
            String guildId = resultSet.getString("guild_id");
            RixaPermission rixaPermission = RixaPermission.fromString(resultSet.getString("permission"));

            if (permissionsMap.containsKey(guildId)) permissions.addAll(permissionsMap.get(guildId));

            if (!permissions.contains(rixaPermission)) permissions.add(rixaPermission);
            permissionsMap.replace(guildId, permissions);
            permissions.clear();
        }
        return permissionsMap;
    }
}
