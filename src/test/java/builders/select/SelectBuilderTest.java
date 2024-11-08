package builders.select;

import org.junit.Test;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.SelectBuilder;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SelectBuilderTest {
    private static final SQLContext context = new SQLContext(EDialect.STANDARD);

    @Test
    public void missingTable() {
        assertThrows(SelectBuilderException.class, () -> {
           String query = new SelectBuilder(context)
                   .selectAll()
                   .build(false);
        });
    }

    @Test
    public void emptyColumns() {
        assertThrows(SelectBuilderException.class, () -> {
           String query = new SelectBuilder(context)
                   .fromTable("sample_table")
                   .build(false);
        });
    }

    @Test
    public void selectAllAsFinalStatement() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT * FROM sample_table;");
    }

    @Test
    public void selectAllNotFinalStatement() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .build(false);
        assertEquals(query, "SELECT * FROM sample_table");
    }

    @Test
    public void fromAlias() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTableWithAlias("sample_table", "alias")
                .build(true);
        assertEquals(query, "SELECT * FROM sample_table AS alias;");
    }

    @Test
    public void selectColumnsIndividually() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .select("col1")
                .select("col2")
                .select("col3")
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT col1,col2,col3 FROM sample_table;");
    }

    @Test
    public void selectColumnsFromList() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .select(Arrays.asList("col1", "col2", "col3"))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT col1,col2,col3 FROM sample_table;");
    }

    @Test
    public void selectColumnsWithAlias() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectWithAlias("col1", "alias1")
                .selectWithAlias("col2", "alias2")
                .selectWithAlias("col3", "alias3")
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT col1 AS alias1,col2 AS alias2,col3 AS alias3 FROM sample_table;");
    }

    @Test
    public void selectWithLimit() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .limit(100)
                .build(true);
        assertEquals(query, "SELECT * FROM sample_table LIMIT 100;");
    }
}
