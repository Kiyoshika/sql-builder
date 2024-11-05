package zweaver.sqlbuilder.datatypes;

import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.util.TypeCastUtil;

import java.util.Arrays;
import java.util.List;

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
        if (this.length == 0)
            return TypeCastUtil.castTo(context, columnName, "VARCHAR", null);

        return TypeCastUtil.castTo(context, columnName, "VARCHAR", List.of(String.valueOf(this.length)));
    }
}
