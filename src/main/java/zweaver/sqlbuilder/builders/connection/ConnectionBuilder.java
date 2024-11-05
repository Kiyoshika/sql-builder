package zweaver.sqlbuilder.builders.connection;

import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.enums.EDriverType;
import zweaver.sqlbuilder.exceptions.ConnectionBuilderException;

/**
 * Build a "standard" JDBC or ODBC connection string.
 *
 * JDBC strings are formatted as jdbc:{driver}://{host}:{port}/{databaseName}. This should work
 * for most common databases but not others.
 */
public class ConnectionBuilder {
    private final SQLContext context;
    private final EDriverType driverType;
    private String hostName;
    private String databaseName;
    private int port;

    public ConnectionBuilder(SQLContext context, EDriverType driverType) throws ConnectionBuilderException {
        if (context == null)
            throw new ConnectionBuilderException("Missing SQL context.");

        this.context = context;
        this.driverType = driverType;
        this.hostName = "localhost";
        this.databaseName = "";
        this.port = -1;
    }

    public ConnectionBuilder setHostname(String hostName) throws ConnectionBuilderException {
        if (hostName == null || hostName.isEmpty())
            throw new ConnectionBuilderException("Hostname can not be empty.");

        this.hostName = hostName;
        return this;
    }

    public ConnectionBuilder setDatabaseName(String databaseName) throws ConnectionBuilderException {
        if (databaseName == null || databaseName.isEmpty())
            throw new ConnectionBuilderException("Database name can not be empty.");

        this.databaseName = databaseName;
        return this;
    }

    public ConnectionBuilder setPort(int port) throws ConnectionBuilderException {
        if (port < 0)
            throw new ConnectionBuilderException("Port can not be negative.");

        this.port = port;
        return this;
    }

    public String build() {
        return switch(this.driverType) {
            case JDBC -> this.toJDBCString();
            case ODBC -> this.toODBCString();
        };
    }

    private String toJDBCString() {
        StringBuilder connectionBuilder = new StringBuilder();
        connectionBuilder.append("jdbc:");
        String driverName = this.getDriverName();
        this.port = this.port == -1 ? this.getDefaultPort() : this.port;
        connectionBuilder
                .append(driverName)
                .append("://")
                .append(this.hostName)
                .append(':')
                .append(this.port);
        if (!this.databaseName.isEmpty())
            connectionBuilder.append('/').append(this.databaseName);
        connectionBuilder.append(';');

        return connectionBuilder.toString();
    }

    private String getDriverName() {
        return switch (this.context.getSqlDialect()) {
            case STANDARD -> "";
            case POSTGRES -> "postgresql";
            case MYSQL -> "mysql";
            case DB2 -> "db2";
            case MSSQL -> "microsoft:sqlserver";
            case MARIADB -> "mariadb";
            case VERTICA -> "vertica";
        };
    }

    private int getDefaultPort() {
        return switch (this.context.getSqlDialect()) {
            case STANDARD -> 0;
            case POSTGRES -> 5432;
            case MYSQL -> 3306;
            case DB2 -> 50000;
            case MSSQL -> 1433;
            case MARIADB -> 3306;
            case VERTICA -> 5433;
        };
    }

    private String toODBCString() {
        return "";
    }
}
