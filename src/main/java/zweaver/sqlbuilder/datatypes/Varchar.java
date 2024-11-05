package zweaver.sqlbuilder.datatypes;

import zweaver.sqlbuilder.SQLContext;

public class Varchar implements IDataType {
    private final SQLContext context;
    private int length;

    public Varchar(SQLContext context) {
        this.context = context;
        this.length = 0;
    }

    public Varchar(SQLContext context, int length) {
        this.context = context;
        this.length = length;
    }

    @Override
    public String castColumn(String columnName) {
        return switch(this.context.getSqlDialect()) {
            case POSTGRES, VERTICA -> {
                if (this.length == 0)
                    yield new StringBuilder()
                            .append(columnName)
                            .append("::VARCHAR")
                            .toString();
                else
                    yield new StringBuilder()
                            .append(columnName)
                            .append("::VARCHAR(")
                            .append(this.length)
                            .append(')')
                            .toString();
            }
            case MSSQL -> {
                if (this.length == 0)
                    yield new StringBuilder()
                            .append("CAST(")
                            .append(columnName)
                            .append(" AS VARCHAR)")
                            .toString();
                else
                    yield new StringBuilder()
                            .append("CAST(")
                            .append(columnName)
                            .append(" AS VARCHAR(")
                            .append(this.length)
                            .append("))")
                            .toString();
            }
            default -> "";
        };
    }
}
