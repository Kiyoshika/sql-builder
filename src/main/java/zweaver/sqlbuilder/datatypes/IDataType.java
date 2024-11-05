package zweaver.sqlbuilder.datatypes;

import zweaver.sqlbuilder.SQLContext;

public interface IDataType {
    public String castColumn(String columnName);
}
