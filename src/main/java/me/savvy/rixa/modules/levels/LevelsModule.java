package me.savvy.rixa.modules.levels;

import lombok.Getter;
import lombok.Setter;
import me.majrly.database.Database;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
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
    private RixaGuild rixaGuild;
    @Getter
    private Map<String, UserData> userData = new HashMap<>();
    @Getter
    @Setter
    private boolean enabled;

    public LevelsModule(RixaGuild rixaGuild) {
        this.rixaGuild = rixaGuild;
        load();
    }

    @Override
    public void load() {
        try {
            Query query = new Query("SELECT * FROM `modules` WHERE `guild_id`=?;");
            query.setString(rixaGuild.getGuild().getId());
            Optional<?> o = Rixa.getDatabase().send(query);
            if (!o.isPresent()) return;
            else if (!(o.get() instanceof ResultSet)) return;
            ResultSet set = (ResultSet) o.get();
            if (set.next()) {
                setEnabled(set.getBoolean("levels"));
            } else {
                Update update = new Update("INSERT INTO `modules` (`guild_id`) VALUES (?);");
                update.setString(rixaGuild.getGuild().getId());
                Rixa.getDatabase().send(update);
                setEnabled(true);
            }
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<UserData> leaderboard(Member member) {
        Database db = Rixa.getDatabase();
        ResultSet rs = null;

        try {
            PreparedStatement ps = db.getConnection().get().prepareStatement("SELECT * FROM `levels` WHERE `guild_id` = ? ORDER BY `experience` DESC;");
            ps.setString(1, member.getGuild().getId());
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<UserData> userDataList = new LinkedList<>();
        try {
            while (rs != null && rs.next()) {
                if (member.getGuild().getMemberById(rs.getString("user_id")) == null) continue;
                UserData userData = ((LevelsModule) rixaGuild.getModule("Levels")).getUserData(rs.getString("user_id"));
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

    @Override
    public void save() {
        DatabaseUtils.update("modules", "levels", "guild_id", enabled, rixaGuild.getGuild().getId());
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
}