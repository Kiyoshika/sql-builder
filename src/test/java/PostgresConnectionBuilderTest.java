import org.junit.Test;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.connection.ConnectionBuilder;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.enums.EDriverType;
import zweaver.sqlbuilder.exceptions.ConnectionBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PostgresConnectionBuilderTest {
    private SQLContext context;

    public PostgresConnectionBuilderTest() {
        this.context = new SQLContext(EDialect.POSTGRES);
    }

    @Test
    public void defaultString_JDBC() throws ConnectionBuilderException {
        String connectionString = new ConnectionBuilder(this.context, EDriverType.JDBC).build();
        assertEquals("jdbc:postgresql://localhost:5432;", connectionString);
    }

    @Test
    public void customPort_JDBC() throws ConnectionBuilderException {
        String connectionString = new ConnectionBuilder(this.context, EDriverType.JDBC)
                .setPort(1111)
                .build();
        assertEquals("jdbc:postgresql://localhost:1111;", connectionString);
    }

    @Test
    public void customDatabase_JDBC() throws ConnectionBuilderException {
        String connectionString = new ConnectionBuilder(this.context, EDriverType.JDBC)
                .setDatabaseName("mydb")
                .build();
        assertEquals("jdbc:postgresql://localhost:5432/mydb;", connectionString);
    }

    @Test
    public void customHostname_JDBC() throws ConnectionBuilderException {
        String connectionString = new ConnectionBuilder(this.context, EDriverType.JDBC)
                .setHostname("my.server")
                .build();
        assertEquals("jdbc:postgresql://my.server:5432;", connectionString);
    }

    @Test
    public void customAll_JDBC() throws ConnectionBuilderException {
        String connectionString = new ConnectionBuilder(this.context, EDriverType.JDBC)
                .setHostname("my.server")
                .setPort(1111)
                .setDatabaseName("mydb")
                .build();
        assertEquals("jdbc:postgresql://my.server:1111/mydb;", connectionString);
    }

    @Test
    public void setInvalidHostname_JDBC() throws ConnectionBuilderException {
        assertThrows(ConnectionBuilderException.class, () -> {
            new ConnectionBuilder(this.context, EDriverType.JDBC).setHostname(null);
        });

        assertThrows(ConnectionBuilderException.class, () -> {
            new ConnectionBuilder(this.context, EDriverType.JDBC).setHostname("");
        });
    }

    @Test
    public void setInvalidPort_JDBC() throws ConnectionBuilderException {
        assertThrows(ConnectionBuilderException.class, () -> {
            new ConnectionBuilder(this.context, EDriverType.JDBC).setPort(-1);
        });
    }

    @Test
    public void setInvalidDatabaseName_JDBC() throws ConnectionBuilderException {
        assertThrows(ConnectionBuilderException.class, () -> {
            new ConnectionBuilder(this.context, EDriverType.JDBC).setDatabaseName(null);
        });

        assertThrows(ConnectionBuilderException.class, () -> {
            new ConnectionBuilder(this.context, EDriverType.JDBC).setDatabaseName("");
        });
    }
}
