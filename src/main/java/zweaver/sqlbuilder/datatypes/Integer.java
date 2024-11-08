package zweaver.sqlbuilder.datatypes;

import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.util.TypeCastUtil;

public final class Integer implements IDataType {
    private final SQLContext context;

    public Integer(SQLContext context) {
        this.context = context;
    }

    @Override
    public String castColumn(String columnName) {
        return TypeCastUtil.castTo(this.context, columnName, "CUSTOM", null);
    }
}
