import org.junit.Test;
import zweaver.sqlbuilder.builders.FilterGroupBuilder;
import zweaver.sqlbuilder.datatypes.Varchar;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.enums.EFilterCondition;
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
                   .build();
        });
    }

    @Test
    public void emptyColumns() {
        assertThrows(SelectBuilderException.class, () -> {
           String query = new SelectBuilder(context)
                   .fromTable("sample_table")
                   .build();
        });
    }

    @Test
    public void selectAll() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT * FROM sample_table;");
    }

    @Test
    public void fromAlias() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTableWithAlias("sample_table", "alias")
                .build();
        assertEquals(query, "SELECT * FROM sample_table AS alias;");
    }

    @Test
    public void selectColumnsIndividually() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .select("col1")
                .select("col2")
                .select("col3")
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1,col2,col3 FROM sample_table;");
    }

    @Test
    public void selectColumnsFromList() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .select(Arrays.asList("col1", "col2", "col3"))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1,col2,col3 FROM sample_table;");
    }

    @Test
    public void selectColumnsWithAlias() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectWithAlias("col1", "alias1")
                .selectWithAlias("col2", "alias2")
                .selectWithAlias("col3", "alias3")
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1 AS alias1,col2 AS alias2,col3 AS alias3 FROM sample_table;");
    }

    @Test
    public void singleFilter() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, 10, false)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 = 10;");
    }

    @Test
    public void selectWithFilters() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, 3, false)
                .filter("col2", EFilterCondition.EQUAL, "text", true)
                .filter("col3", EFilterCondition.NOT_EQUAL, false, false)
                .filter("col4", EFilterCondition.GREATER_THAN, 10, false)
                .filter("col5", EFilterCondition.GREATER_THAN_EQUAL, 20, false)
                .filter("col6", EFilterCondition.LESS_THAN, 30, false)
                .filter("col7", EFilterCondition.LESS_THAN_EQUAL, 40, false)
                .filter("col8", EFilterCondition.LIKE, "text%", true)
                .filter("col9", EFilterCondition.ILIKE, "text%", true)
                .build();

        String expectedQuery = new StringBuilder()
                .append("SELECT * FROM sample_table").append(' ')
                .append("WHERE col1 = 3").append(' ')
                .append("AND col2 = 'text'").append(' ')
                .append("AND col3 != false").append(' ')
                .append("AND col4 > 10").append(' ')
                .append("AND col5 >= 20").append(' ')
                .append("AND col6 < 30").append(' ')
                .append("AND col7 <= 40").append(' ')
                .append("AND col8 LIKE 'text%'").append(' ')
                .append("AND col9 ILIKE 'text%'")
                .append(';')
                .toString();

        assertEquals(query, expectedQuery);
    }

    @Test
    public void filterOrConjunction() throws SelectBuilderException {
        // NOTE: it's not actually recommended to use this pattern for complex filters,
        // see the filter(new FilterGroupBuilder()) pattern
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, 10, false)
                .or()
                .filter("col1", EFilterCondition.EQUAL, 20, false)
                .filter("col2", EFilterCondition.EQUAL, 30, false)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 = 10 OR col1 = 20 AND col2 = 30;");
    }

    @Test
    public void filterSanitizeText() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, "quot'ed stri''ng", true)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 = 'quot''ed stri''ng';");

        query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, "quote at end'", true)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 = 'quote at end''';");
    }

    @Test
    public void filterGroupAnyOf() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, 10, false)
                .filter(new FilterGroupBuilder()
                        .addFilter("col2", EFilterCondition.EQUAL, 20, false)
                        .addFilter("col3", EFilterCondition.LESS_THAN, 30, false)
                        .addFilter("col4", EFilterCondition.LIKE, "text%", true)
                        .anyOf())
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 = 10 AND (col2 = 20 OR col3 < 30 OR col4 LIKE 'text%');");
    }

    @Test
    public void filterGroupAllOf() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, 10, false)
                .filter(new FilterGroupBuilder()
                        .addFilter("col2", EFilterCondition.EQUAL, 20, false)
                        .addFilter("col3", EFilterCondition.LESS_THAN, 30, false)
                        .addFilter("col4", EFilterCondition.LIKE, "text%", true)
                        .allOf())
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 = 10 AND (col2 = 20 AND col3 < 30 AND col4 LIKE 'text%');");
    }

    @Test
    public void selectWithLimit() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .limit(100)
                .build();
        assertEquals(query, "SELECT * FROM sample_table LIMIT 100;");
    }

    @Test
    public void filterInSingleValueNonQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.IN, 10, false)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 IN (10);");
    }

    @Test
    public void filterInSingleValueQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.IN, 10, true)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 IN ('10');");
    }

    @Test
    public void filterNotInSingleValueNonQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.NOT_IN, 10, false)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 NOT IN (10);");
    }

    @Test
    public void filterNotInSingleValueQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.NOT_IN, 10, true)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 NOT IN ('10');");
    }

    @Test
    public void filterInMultipleValuesNonQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.IN, Arrays.asList(10, 20, 30), false)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 IN (10,20,30);");
    }

    @Test
    public void filterInMultipleValuesQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.IN, Arrays.asList(10, 20, 30), true)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 IN ('10','20','30');");
    }

    @Test
    public void filterNotInMultipleValuesNonQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.NOT_IN, Arrays.asList(10, 20, 30), false)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 NOT IN (10,20,30);");
    }

    @Test
    public void filterNotInMultipleValuesQuoted() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.NOT_IN, Arrays.asList(10, 20, 30), true)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 NOT IN ('10','20','30');");
    }

    @Test
    public void selectAndCastToVarcharNoLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1::VARCHAR FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR) FROM sample_table;");
    }

    @Test
    public void selectAndCastWithAliasToVarcharNoLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1::VARCHAR AS c1 FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR) AS c1 FROM sample_table;");
    }

    @Test
    public void selectAndCastToVarcharWithLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1::VARCHAR(100) FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCast("col1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR(100)) FROM sample_table;");
    }

    @Test
    public void selectAndCastWithAliasToVarcharWithLength() throws SelectBuilderException {
        SQLContext ctx = new SQLContext(EDialect.POSTGRES); // same as: VERTICA
        String query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT col1::VARCHAR(100) AS c1 FROM sample_table;");

        // need to rebuild the query due to different context
        ctx.setSqlDialect(EDialect.MSSQL);
        query = new SelectBuilder(ctx)
                .selectAndCastWithAlias("col1", "c1", new Varchar(ctx, 100))
                .fromTable("sample_table")
                .build();
        assertEquals(query, "SELECT CAST(col1 AS VARCHAR(100)) AS c1 FROM sample_table;");
    }
}
