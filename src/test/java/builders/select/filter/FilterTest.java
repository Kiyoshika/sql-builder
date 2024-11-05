package builders.select.filter;

import org.junit.Test;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.FilterGroupBuilder;
import zweaver.sqlbuilder.builders.SelectBuilder;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.enums.EFilterCondition;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FilterTest {
    private final SQLContext context;

    public FilterTest() {
        this.context = new SQLContext(EDialect.STANDARD);
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
    public void singleFilter() throws SelectBuilderException {
        String query = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table")
                .filter("col1", EFilterCondition.EQUAL, 10, false)
                .build();
        assertEquals(query, "SELECT * FROM sample_table WHERE col1 = 10;");
    }

    @Test
    public void multipleFilters() throws SelectBuilderException {
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
}
