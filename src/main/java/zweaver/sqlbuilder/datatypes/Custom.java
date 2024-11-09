package zweaver.sqlbuilder.datatypes;

import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.enums.EDialect;
import zweaver.sqlbuilder.util.TypeCastUtil;

public final class Custom implements IDataType {
    private final String typeName;
    private boolean isTextType;

    public Custom(String typeName, boolean isTextType) {
        this.typeName = typeName;
        this.isTextType = isTextType;
    }

    @Override
    public boolean isTextType() { return this.isTextType; }

    public void setIsTextType(boolean isTextType) { this.isTextType = isTextType; }

    @Override
    public String castColumn(String columnName) {
        return TypeCastUtil.castTo(new SQLContext(EDialect.STANDARD), columnName, typeName, null);
    }

    @Override
    public String toString() { return this.typeName.toUpperCase(); }
}
