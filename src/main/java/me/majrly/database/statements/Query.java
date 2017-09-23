package me.majrly.database.statements;

/**
 * Apart of the Database API to differentiate between various Statements
 *
 * @author Majrly
 * @since 1.0.0
 */
public class Query extends Statement {

    public Query(String sql) {
        super(sql);
    }
}