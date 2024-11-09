package zweaver.sqlbuilder.datatypes;

import zweaver.sqlbuilder.SQLContext;

public interface IDataType {
    public boolean isTextType();
    public String castColumn(String columnName);
    public String toString();
}
