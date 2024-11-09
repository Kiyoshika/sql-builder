package builders;

import org.junit.Test;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.CreateTableBuilder;
import zweaver.sqlbuilder.datatypes.Integer;
import zweaver.sqlbuilder.datatypes.Varchar;
import zweaver.sqlbuilder.entities.TableColumn;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.exceptions.CreateTableBuilderException;
import zweaver.sqlbuilder.exceptions.TableColumnException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CreateTableBuilderTest {
    private final SQLContext context;

    public CreateTableBuilderTest() {
        this.context = new SQLContext(EDialect.STANDARD);
    }

    @Test
    public void emptyTableName() {
        assertThrows(CreateTableBuilderException.class, () -> new CreateTableBuilder(context, null, false));
    }

    @Test
    public void noColumns() {
        assertThrows(CreateTableBuilderException.class, () -> new CreateTableBuilder(context, "sample_table", false).build());
    }

    @Test
    public void createTable() throws TableColumnException, CreateTableBuilderException {
        String table = new CreateTableBuilder(context, "sample_table", false)
                .addPrimaryKey("id")
                .addColumn(new TableColumn("col1", new Varchar(context, 100), false))
                .addColumn(new TableColumn("col2", new Integer(context), true))
                .addColumn(new TableColumn("col3", new Integer(context), false, 25))
                .build();

        assertEquals("CREATE TABLE sample_table ( id SERIAL PRIMARY KEY, col1 VARCHAR(100) NOT NULL, col2 INTEGER, col3 INTEGER NOT NULL DEFAULT VALUE 25);", table);
    }

    @Test
    public void createTableCheckNotExists() throws TableColumnException, CreateTableBuilderException {
        String table = new CreateTableBuilder(context, "sample_table", true)
                .addPrimaryKey("id")
                .addColumn(new TableColumn("col1", new Varchar(context, 100), false))
                .addColumn(new TableColumn("col2", new Integer(context), true))
                .build();

        assertEquals("CREATE TABLE IF NOT EXISTS sample_table ( id SERIAL PRIMARY KEY, col1 VARCHAR(100) NOT NULL, col2 INTEGER);", table);
    }

    @Test
    public void duplicateColumnNames() {
        assertThrows(CreateTableBuilderException.class, () -> new CreateTableBuilder(context, "sample_table", true)
                .addColumn(new TableColumn("col1", new Varchar(context, 100), false))
                .addColumn(new TableColumn("col1", new Integer(context), true))
                .build());
    }

    @Test
    public void duplicateColumnNameWithPrimaryKey() {
        assertThrows(CreateTableBuilderException.class, () -> new CreateTableBuilder(context, "sample_table", true)
                .addPrimaryKey("id")
                .addColumn(new TableColumn("id", new Varchar(context, 100), false))
                .build());
    }

    @Test
    public void tooManyPrimaryKeys() {
        assertThrows(CreateTableBuilderException.class, () -> new CreateTableBuilder(context, "sample_table", true)
                .addPrimaryKey("id")
                .addPrimaryKey("id2")
                .build());
    }
}
