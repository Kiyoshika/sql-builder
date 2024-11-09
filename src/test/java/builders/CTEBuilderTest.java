package builders;

import org.junit.Test;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.CTEBuilder;
import zweaver.sqlbuilder.builders.SelectBuilder;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.exceptions.CTEBuilderException;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CTEBuilderTest {
    private final SQLContext context;
    private final SelectBuilder sampleQuery;

    public CTEBuilderTest() {
        this.context = new SQLContext(EDialect.STANDARD);
        this.sampleQuery = new SelectBuilder(this.context).selectAll().fromTable("one");
    }

    @Test
    public void emptyName() {
        assertThrows(CTEBuilderException.class, () -> new CTEBuilder()
                .addTable("", sampleQuery)
                .setQuery(sampleQuery));
    }

    @Test
    public void nullName() {
        assertThrows(CTEBuilderException.class, () -> new CTEBuilder()
                .addTable(null, sampleQuery)
                .setQuery(sampleQuery));
    }

    @Test
    public void nullQuery() {
        assertThrows(CTEBuilderException.class, () -> new CTEBuilder()
                .addTable("name", null)
                .setQuery(sampleQuery));
    }

    @Test
    public void emptyTables() {
        assertThrows(CTEBuilderException.class, () -> new CTEBuilder()
                .setQuery(sampleQuery)
                .build());
    }

    @Test
    public void emptyMainQuery() {
        assertThrows(CTEBuilderException.class, () -> new CTEBuilder()
                .addTable("name", sampleQuery)
                .setQuery(null)
                .build());
    }

    @Test
    public void duplicateTableNames() {
        assertThrows(CTEBuilderException.class, () -> new CTEBuilder()
                .addTable("one",
                        new SelectBuilder(context)
                                .selectAll()
                                .fromTable("table_one"))
                .addTable("one",
                        new SelectBuilder(context)
                                .selectAll()
                                .fromTable("table_two"))
                .setQuery(sampleQuery)
                .build());
    }

    @Test
    public void mainQueryReferencesNonExistingTable() {
        assertThrows(CTEBuilderException.class, () -> new CTEBuilder()
                .addTable("random",
                        new SelectBuilder(context)
                                .selectAll()
                                .fromTable("table_one"))
                .setQuery(sampleQuery)
                .build());
    }

    @Test
    public void singleCte() throws CTEBuilderException, SelectBuilderException {
        String cte = new CTEBuilder()
                .addTable("one",
                        new SelectBuilder(context)
                                .selectAll()
                                .fromTable("table_one"))
                .setQuery(sampleQuery)
                .build();

        assertEquals("WITH one AS ( SELECT * FROM table_one ) SELECT * FROM one;", cte);
    }

    @Test
    public void doubleCte() throws CTEBuilderException, SelectBuilderException {
        String cte = new CTEBuilder()
                .addTable("one",
                        new SelectBuilder(context)
                                .selectAll()
                                .fromTable("table_one"))
                .addTable("two",
                        new SelectBuilder(context)
                                .selectAll()
                                .fromTable("table_two"))
                .setQuery(sampleQuery)
                .build();

        assertEquals("WITH one AS ( SELECT * FROM table_one ), two AS ( SELECT * FROM table_two ) SELECT * FROM one;", cte);
    }
}
