package zweaver.sqlbuilder.entities;

import zweaver.sqlbuilder.datatypes.IDataType;
import zweaver.sqlbuilder.datatypes.Varchar;
import zweaver.sqlbuilder.exceptions.TableColumnException;

public class TableColumn {
    private final String name;
    private final IDataType dataType;
    private final boolean isNullable;
    private final Object defaultValue;

    public TableColumn(String name, IDataType dataType, boolean isNullable) throws TableColumnException {
        if (name == null || name.isEmpty())
            throw new TableColumnException("Column name can not be null.");

        this.name = name;
        this.dataType = dataType;
        this.isNullable = isNullable;
        this.defaultValue = null;
    }

    public TableColumn(String name, IDataType dataType, boolean isNullable, Object defaultValue) throws TableColumnException {
        if (name == null || name.isEmpty())
            throw new TableColumnException("Column name can not be null.");

        this.name = name;
        this.dataType = dataType;
        this.isNullable = isNullable;
        this.defaultValue = defaultValue;
    }

    public String getName() { return this.name; }

    public IDataType getDataType() { return this.dataType; }

    public boolean isNullable() { return this.isNullable; }

    public Object getDefaultValue() { return this.defaultValue; }

    @Override
    public String toString() {
        StringBuilder column = new StringBuilder();
        column.append(this.getName()).append(' ');
        column.append(this.getDataType());
        Object defaultValue = this.getDefaultValue();

        if (!this.isNullable())
            column.append(" NOT NULL");

        if (defaultValue != null) {
            column.append(" DEFAULT VALUE ");

            if (this.getDataType().isTextType())
                column.append("'").append(defaultValue).append("'");
            else
                column.append(defaultValue);
        }

        return column.toString();
    }
}
