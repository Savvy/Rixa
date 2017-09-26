package me.savvy.rixa.modules.levels;

import lombok.Getter;
import me.majrly.database.Database;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.user.UserData;
import me.savvy.rixa.modules.RixaModule;
import me.savvy.rixa.utils.DatabaseUtils;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Timber on 5/23/2017.
 */
public class LevelsModule implements RixaModule {

    @Getter
    private final RixaGuild rixaGuild;
    @Getter
    private Map<String, UserData> userData = new HashMap<>();
    @Getter
    private boolean enabled;

    public LevelsModule(RixaGuild rixaGuild) {
        this.rixaGuild = rixaGuild;
        enabled = true;
        load();
    }

    private List<UserData> leaderboard(Member member) {
        Database db = Rixa.getDatabase();
        ResultSet rs = null;

        try {
            rs = db.getConnection().get().prepareStatement(String.format("SELECT * FROM `levels` WHERE `guild_id` = '%s' ORDER BY `experience` DESC;", member.getGuild().getId())).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<UserData> userDataList = new LinkedList<>();
        try {
            while (rs != null && rs.next()) {
                if (member.getGuild().getMemberById(rs.getString("user_id")) == null) continue;
                UserData userData = rixaGuild.getLevelsModule().getUserData(rs.getString("user_id"));
                userDataList.add(userData);
            }
            rs.getStatement().close();
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userDataList;
    }

    public MessageBuilder leaderboard(Member member, int page) {
        int sizePerPage = 10;
        if (page < 1) {
            page = 1;
        }
        List<UserData> userData = leaderboard(member);
        int maxPages = userData.size() / sizePerPage + (userData.size() % sizePerPage > 0 ? 1 : 0);
        if (page > maxPages) {
            return null;
        }
        /*int from = Math.max(0, page * sizePerPage);
        int to = Math.min(userData.size(), (page + 2) * sizePerPage);*/
        int start = Math.min(Math.max(sizePerPage * (page - 1), 0), userData.size());
        int end = Math.min(Math.max(sizePerPage * page, start), userData.size());
        List<UserData> userList = userData.subList(start, end);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            UserData user = userList.get(i);
            if (user == null) continue;
            stringBuilder
                    .append("`")
                    .append(i + start + 1/*(page > 1) ? ((i + 1) * 10) : i + 1*/)
                    .append(")` ")
                    .append(user.getUser().getName())
                    .append("#").append(user.getUser().getDiscriminator())
                    .append(" (Lvl. ").append(user.getLevel()).append(")")
                    .append("\n");
        }
        MessageBuilder builder = new MessageBuilder(stringBuilder.toString());
        builder.footer("Page: (" + page + " / " + maxPages + ")", member.getGuild().getIconUrl());
        return builder.setColor(member.getColor()).setTitle(String.format("Leaderboard: %s", member.getGuild().getName()));
    }

    @Override
    public String getName() {
        return "Levels";
    }

    @Override
    public String getDescription() {
        return "Rixa levels module.";
    }

    public void registerUser(UserData userData) {
        if (getUserData().containsKey(userData.getUser().getId())) {
            return;
        }
        getUserData().put(userData.getUser().getId(), userData);
    }

    public UserData getUserData(String key) {
        checkUser(key);
        return getUserData().get(key);
    }

    private void checkUser(String key) {
        if (getUserData().containsKey(key)) {
            return;
        }
        new UserData
                (getRixaGuild().getGuild().getJDA().getUserById(key),
                        getRixaGuild().getGuild());
    }

    private void load() {
        if (!(checkExists())) {
            this.enabled = true;
            insert();
        }
        String query = "SELECT `levels` FROM `modules` WHERE `guild_id` = ?;";
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = Rixa.getDatabase().getConnection().get().prepareStatement(query);
            ps.setString(1, getRixaGuild().getGuild().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                this.enabled = rs.getBoolean("levels");
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkExists() {
        Result r = Result.FALSE;
        try {
            Query query = new Query("SELECT `guild_id` FROM `modules` WHERE `guild_id` = ?;");
            query.setString(rixaGuild.getGuild().getId());
            Optional<?> optional = Rixa.getDatabase().send(query);
            if (!optional.isPresent()) r = Result.ERROR;
            if (!(optional.get() instanceof ResultSet)) r = Result.ERROR;
            ResultSet set = (ResultSet) optional.get();
            if (r != Result.ERROR) {
                if (set.next()) {
                    r = Result.TRUE;
                } else {
                    r = Result.FALSE;
                }
            }
            set.close();
            return r == Result.TRUE;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void insert() {
        String query = "INSERT INTO `modules` (`guild_id`) VALUES (?);";
        Update update = new Update(query);
        update.setString(rixaGuild.getGuild().getId());
        Rixa.getDatabase().send(update);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        DatabaseUtils.update("modules", "levels", "guild_id", enabled, rixaGuild.getGuild().getId());
    }
}
