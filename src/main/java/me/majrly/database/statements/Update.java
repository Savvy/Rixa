package me.majrly.database.statements;

/**
 * Apart of the Database API to differentiate between various Statements
 *
 * @author Majrly
 * @since 1.0.0
 */
public class Update extends Statement {

    public Update(String sql) {
        super(sql);
    }
}