package zweaver.sqlbuilder.datatypes;

import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.util.TypeCastUtil;

public final class Custom implements IDataType {
    private final SQLContext context;
    private final String typeName;

    public Custom(SQLContext context, String typeName) {
        this.context = context;
        this.typeName = typeName;
    }

    @Override
    public String castColumn(String columnName) {
        return TypeCastUtil.castTo(context, columnName, typeName, null);
    }
}
