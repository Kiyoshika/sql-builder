package zweaver.sqlbuilder.builders.connection.strings;

import zweaver.sqlbuilder.enums.EDriverType;

public abstract class BaseConnectionString {
    private EDriverType driverType;
    public BaseConnectionString(EDriverType driverType) {
        this.driverType = driverType;
    }

    public String toString() {
        return switch(this.driverType) {
            case JDBC -> this.toJDBCString();
            case ODBC -> this.toODBCString();
        };
    }

    protected abstract String toJDBCString();

    protected abstract String toODBCString();
}
