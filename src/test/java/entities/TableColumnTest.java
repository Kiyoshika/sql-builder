package entities;

import org.junit.Test;
import zweaver.sqlbuilder.datatypes.Custom;
import zweaver.sqlbuilder.datatypes.Integer;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.datatypes.Varchar;
import zweaver.sqlbuilder.entities.TableColumn;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.exceptions.TableColumnException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TableColumnTest {
    private final SQLContext context;

    public TableColumnTest() {
        this.context = new SQLContext(EDialect.STANDARD);
    }

    @Test
    public void emptyName() {
        assertThrows(TableColumnException.class, () -> new TableColumn(null, new Integer(context), false));
        assertThrows(TableColumnException.class, () -> new TableColumn("", new Integer(context), false));
    }

    @Test
    public void nullableChecks() throws TableColumnException {
        String column = new TableColumn("col1", new Custom("CUSTOM", false), false).toString();
        assertEquals("col1 CUSTOM NOT NULL", column);
        column = new TableColumn("col1", new Custom("CUSTOM", false), true).toString();
        assertEquals("col1 CUSTOM", column);
    }

    @Test
    public void defaultValueChecks() throws TableColumnException {
        String column = new TableColumn("col1", new Integer(context), true, 25).toString();
        assertEquals("col1 INTEGER DEFAULT VALUE 25", column);
        column = new TableColumn("col1", new Varchar(context, 100), true, "default text").toString();
        assertEquals("col1 VARCHAR(100) DEFAULT VALUE 'default text'", column);
    }
}
