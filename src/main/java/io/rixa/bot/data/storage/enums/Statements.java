package io.rixa.bot.data.storage.enums;

import lombok.Getter;

public enum Statements {

    /*
    Select statements
     */
    SELECT_CORE("SELECT * FROM `core` WHERE `guild_id` = ?"),
    SELECT_MODULE_STATUS("SELECT `{module_name}` FROM `modules` WHERE `guild_id` = ?"),


    /*
    Insert Statements
     */
    INSERT_CORE("INSERT INTO `core` (`guild_id`, `guild_name`, `description`, `keywords`) VALUES (?, ?, ?, ?)"),

    /*
    Count Statements
     */
    COUNT_CORE("SELECT COUNT(*) FROM `%s` WHERE `%s` = ?");

    /*
    Delete Statements
     */


    /*
    Create Table Statements
     */

    @Getter  private String statement;
    public String getStatement(String key, String value) {
        return getStatement().replace(key, value);
    }
    Statements(String s) {
        this.statement = s;
    }
}
