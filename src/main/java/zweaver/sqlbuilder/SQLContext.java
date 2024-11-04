package zweaver.sqlbuilder;

import zweaver.sqlbuilder.enums.EDialect;

public class SQLContext {
    private final EDialect sqlDialect;

    public SQLContext(EDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
}
