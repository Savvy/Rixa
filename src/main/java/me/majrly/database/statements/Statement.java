package me.majrly.database.statements;

import me.majrly.database.params.Parameter;

import java.sql.Blob;
import java.sql.Date;
import java.util.HashMap;

/**
 * Apart of the Database API to create MySQL statements
 *
 * @author Majrly
 * @since 1.0.0
 */
public class Statement {

    // Variables
    private String sql;
    private int amount = 1;
    private HashMap<Integer, Parameter> parameters = new HashMap<Integer, Parameter>();

    /**
     * Used to create SQL statements
     *
     * @param sql The SQL string
     * @since 1.0.0
     */
    public Statement(String sql) {
        this.sql = sql;
    }

    /**
     * Escape a string
     *
     * @param data The string you want to escape
     * @since 1.0.0
     */
    public void setString(String data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.STRING, data));
    }

    /**
     * Escape a string
     *
     * @param data The Blob you want to escape
     * @since 1.0.0
     */
    public void setBlob(Blob data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.BLOB, data));
    }

    /**
     * Escape a double
     *
     * @param data The double you want to escape
     * @since 1.0.0
     */
    public void setDouble(double data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.DOUBLE, data));
    }

    /**
     * Escape an integer
     *
     * @param data The integer you want to escape
     * @since 1.0.0
     */
    public void setInteger(int data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.INTEGER, data));
    }

    /**
     * Escape a float
     *
     * @param data The float you want to escape
     * @since 1.0.0
     */
    public void setFloat(float data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.FLOAT, data));
    }

    /**
     * Escape a date
     *
     * @param data The date you want to escape
     * @since 1.0.0
     */
    public void setDate(Date data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.DATE, data));
    }

    /**
     * Escape a boolean
     *
     * @param data The boolean you want to escape
     * @since 1.0.0
     */
    public void setBoolean(boolean data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.BOOLEAN, data));
    }

    /**
     * Escape an object
     *
     * @param data The object you want to escape
     * @since 1.0.0
     */
    public void setObject(Object data) {
        this.parameters.put(amount++, new Parameter(Parameter.Type.OBJECT, data));
    }

    // Getters
    public String getSQL() {
        return sql;
    }

    public HashMap<Integer, Parameter> getParameters() {
        return parameters;
    }
}