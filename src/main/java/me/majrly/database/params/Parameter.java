package me.majrly.database.params;

/**
 * Apart of the Database API to set parameters (AKA those little question marks in sql statements)
 *
 * @author Majrly
 * @since 1.0.0
 */
public class Parameter {

    // Variables
    private Object data;
    private Type type = Type.OBJECT;

    /**
     * Used to set parameters
     *
     * @param type The type of data you want sent
     * @param data The data
     * @since 1.0.0
     */
    public Parameter(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Used to set parameters
     *
     * @param data The object data
     * @since 1.0.0
     */
    public Parameter(Object data) {
        this.data = data;
    }

    /**
     * The type of data you want sent
     *
     * @author Majrly
     * @since 1.0.0
     */
    public enum Type {
        STRING, BLOB, INTEGER, DOUBLE, FLOAT, DATE, LONG, BOOLEAN, OBJECT;
    }

    public Object getData() {
        return data;
    }

    public Type getType() {
        return type;
    }
}