package zweaver.sqlbuilder.builders.connection.strings;

import zweaver.sqlbuilder.builders.connection.strings.BaseConnectionString;
import zweaver.sqlbuilder.enums.EDriverType;

public final class PostgresConnectionString extends BaseConnectionString {
    private final EDriverType driverType;
    private final String hostName;
    private int port;
    private final String databaseName;

    public PostgresConnectionString(EDriverType driverType, String hostName, int port, String databaseName) {
        super(driverType);
        this.driverType = driverType;
        this.hostName = hostName;
        this.port = port;
        this.databaseName = databaseName;
    }

    @Override
    protected String toJDBCString() {
        StringBuilder connectionBuilder = new StringBuilder()
                .append("postgresql://")
                .append(hostName).append(':');

        if (port == -1)
            port = 5432; // default PSQL port
        connectionBuilder.append(port);

        if (!databaseName.isEmpty())
            connectionBuilder.append('/').append(databaseName);

        return connectionBuilder.toString();
    }

    @Override
    protected String toODBCString() {
        return "";
    }
}
