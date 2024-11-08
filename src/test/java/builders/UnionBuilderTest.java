package builders;

import org.junit.Test;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.builders.SelectBuilder;
import zweaver.sqlbuilder.builders.UnionBuilder;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;
import zweaver.sqlbuilder.exceptions.UnionBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UnionBuilderTest {
    private final SQLContext context;
    private final SelectBuilder sampleQuery;

    public UnionBuilderTest() {
        this.context = new SQLContext(EDialect.STANDARD);
        this.sampleQuery = new SelectBuilder(context)
                .selectAll()
                .fromTable("sample_table");
    }

    @Test
    public void nullInitialQuery() throws UnionBuilderException {
        assertThrows(UnionBuilderException.class, () -> new UnionBuilder(null).build());
    }

    @Test
    public void nullUnion() throws UnionBuilderException {
        assertThrows(UnionBuilderException.class, () -> new UnionBuilder(sampleQuery).union(null).build());
    }

    @Test
    public void nullUnionAll() throws UnionBuilderException {
        assertThrows(UnionBuilderException.class, () -> new UnionBuilder(sampleQuery).unionAll(null).build());
    }

    @Test
    public void unionAndUnionAll() throws UnionBuilderException, SelectBuilderException {
        String union = new UnionBuilder(sampleQuery)
                .union(sampleQuery)
                .unionAll(sampleQuery)
                .build();
        assertEquals("SELECT * FROM sample_table UNION SELECT * FROM sample_table UNION ALL SELECT * FROM sample_table;", union);
    }
}
