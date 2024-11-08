package builders.select.typecast;

import org.junit.Test;
import zweaver.sqlbuilder.datatypes.Integer;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.SelectBuilder;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;

import static org.junit.Assert.assertEquals;

public class IntegerCastTest {
    @Test
    public void selectAndCastToIntegerNoLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Integer(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1::CUSTOM FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Integer(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT CAST(col1 AS CUSTOM) FROM sample_table;");
    }

    @Test
    public void selectAndCastWithAliasToIntegerNoLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Integer(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1::CUSTOM AS c1 FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Integer(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT CAST(col1 AS CUSTOM) AS c1 FROM sample_table;");
    }
}
