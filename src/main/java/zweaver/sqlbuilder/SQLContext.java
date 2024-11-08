package zweaver.sqlbuilder;

import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.enums.EDriverType;

public final class SQLContext {
    private EDialect sqlDialect;

    public SQLContext(EDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    public void setSqlDialect(EDialect sqlDialect) { this.sqlDialect = sqlDialect; }

    public EDialect getSqlDialect() { return this.sqlDialect; }
}
