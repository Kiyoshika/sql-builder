package zweaver.sqlbuilder.builders.connection;

import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.connection.strings.PostgresConnectionString;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.enums.EDriverType;
import zweaver.sqlbuilder.exceptions.ConnectionBuilderException;

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
        String prefix = this.getConnectionPrefix();
        String suffix = switch(this.context.getSqlDialect()) {
            case EDialect.STANDARD -> "";
            case EDialect.POSTGRES -> new PostgresConnectionString(driverType, hostName, port, databaseName).toString();
            default -> "";
        };

        return new StringBuilder().append(prefix).append(suffix).append(';').toString();
    }

    private String getConnectionPrefix() {
        return switch (this.driverType) {
            case EDriverType.JDBC -> "jdbc:";
            case EDriverType.ODBC -> "odbc:";
        };
    }
}
