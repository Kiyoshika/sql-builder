package builders.select.typecast;

import org.junit.Test;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.SelectBuilder;
import zweaver.sqlbuilder.datatypes.Varchar;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;

import static org.junit.Assert.assertEquals;

public class VarcharCastTest {
    @Test
    public void selectAndCastToVarcharNoLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT col1::VARCHAR FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR) FROM sample_table;");
    }

    @Test
    public void selectAndCastWithAliasToVarcharNoLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT col1::VARCHAR AS c1 FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR) AS c1 FROM sample_table;");
    }

    @Test
    public void selectAndCastToVarcharWithLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT col1::VARCHAR(100) FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR(100)) FROM sample_table;");
    }

    @Test
    public void selectAndCastWithAliasToVarcharWithLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT col1::VARCHAR(100) AS c1 FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build(true);
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR(100)) AS c1 FROM sample_table;");
    }
}
