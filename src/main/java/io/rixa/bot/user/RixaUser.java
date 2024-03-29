package io.rixa.bot.user;

import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.data.storage.enums.Statements;
import io.rixa.bot.guild.enums.PermissionType;
import io.rixa.bot.pagination.ObjectPagination;
import io.rixa.bot.user.mapper.UserPermissionsMapper;
import io.rixa.bot.utils.DiscordUtils;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RixaUser {

    @Getter
    private User user;
    @Getter
    private Map<String, Integer> levels;
    @Getter
    private Map<String, List<RixaPermission>> permissions;
    @Getter
    private long last_awarded;

    public RixaUser(User user) {
        this.user = user;
        levels = new HashMap<>();
        permissions = new HashMap<>();
        last_awarded = (System.currentTimeMillis() - 60000);
        load();
    }

    private void load() {
        int count = DatabaseAdapter.getInstance().get().queryForObject
                ("SELECT COUNT(*) FROM `levels` WHERE `user_id` = ?", new Object[] { user.getId() },  Integer.class);
        if (count > 0) {
            DatabaseAdapter.getInstance().get().queryForObject("SELECT * FROM `levels` WHERE `user_id` = ?",
                    new Object[]{user.getId()},
                    (resultSet, i) -> {
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            levels.put(resultSet.getString("guild_id"), resultSet.getInt("experience"));
                        }
                        return 0;
                    });
        }
        permissions.clear();
        permissions.putAll(DatabaseAdapter.getInstance().get().query("SELECT * FROM `permissions` WHERE `type` = ? AND `object_id` = ?",
                new Object[]{PermissionType.USER, user.getId()}, new UserPermissionsMapper()));
    }

    public void save() {
        levels.forEach((guildId, integer) -> {
            int i = DatabaseAdapter.getInstance().get().queryForObject
                    ("SELECT COUNT(*) FROM `levels` WHERE `guild_id` = ? AND `user_id` = ?", new Object[]{
                            guildId, user.getId()
                    }, Integer.class);
            if (i > 0) {
                DatabaseAdapter.getInstance().get().update(
                        "UPDATE `levels` SET `experience` = ? WHERE `guild_id` = ? AND `user_id` = ?", integer, guildId,
                        user.getId());
                return;
            }
            DatabaseAdapter.getInstance().get().update
                    ("INSERT INTO `levels` (guild_id, user_id, experience) VALUES (?, ?, ?);", guildId, user.getId(), integer);
        });
    }

    public boolean awardIfCan(Guild guild) {
        long b = ((System.currentTimeMillis() - last_awarded) / 1000);
        if (b < 60) {
            return false;
        }
        int amountAdding = ThreadLocalRandom.current().nextInt(15, 25);
        int exp = levels.getOrDefault(guild.getId(), 0);
        int currentLevel = DiscordUtils.getLevelFromExperience(exp);
        if (levels.containsKey(guild.getId())) {
            levels.replace(guild.getId(), exp + amountAdding);
        } else {
            levels.put(guild.getId(), exp + amountAdding);
        }
        this.last_awarded = System.currentTimeMillis();
        return currentLevel < DiscordUtils.getLevelFromExperience(levels.get(guild.getId()));
    }

    public void addPermission(String guildId, RixaPermission rixaPermission) {
        if (!permissions.containsKey(guildId)) {
            permissions.put(guildId, Collections.singletonList(rixaPermission));
            return;
        }
        List<RixaPermission> permissionsList = permissions.get(guildId);
        if (permissionsList.contains(rixaPermission)) return;
        permissionsList.add(rixaPermission);
        permissions.replace(guildId, permissionsList);
    }

    public void removePermission(String guildId, RixaPermission rixaPermission) {
        if (!permissions.containsKey(guildId)) return;
        List<RixaPermission> permissionsList = permissions.get(guildId);
        if (!permissionsList.contains(rixaPermission)) return;
        permissionsList.remove(rixaPermission);
        permissions.replace(guildId, permissionsList);
    }

    public boolean hasPermission(String guildId, RixaPermission rixaPermission) {
        return permissions.containsKey(guildId) && permissions.get(guildId).contains(rixaPermission);
    }

    public int getLevels(String id) {
        return levels.getOrDefault(id, 0);
    }
}
