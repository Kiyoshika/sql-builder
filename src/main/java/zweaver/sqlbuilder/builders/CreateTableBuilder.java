package zweaver.sqlbuilder.builders;

import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.entities.TableColumn;
import zweaver.sqlbuilder.exceptions.CreateTableBuilderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CreateTableBuilder {
    private final SQLContext context;
    private final String name;
    private final boolean checkIfNotExists;
    private String primaryKeyName;
    private final List<TableColumn> columns;

    public CreateTableBuilder(SQLContext context, String name, boolean checkIfNotExists) throws CreateTableBuilderException {
        this.context = context;
        if (name == null || name.isEmpty())
            throw new CreateTableBuilderException("Table name can not be empty.");
        this.name = name;
        this.checkIfNotExists = checkIfNotExists;
        this.primaryKeyName = null;
        this.columns = new ArrayList<>();
    }

    public CreateTableBuilder addPrimaryKey(String primaryKeyName) throws CreateTableBuilderException {
        if (this.primaryKeyName != null)
            throw new CreateTableBuilderException("Primary key already exists.");
        this.primaryKeyName = primaryKeyName;
        return this;
    }

    public CreateTableBuilder addColumn(TableColumn column) throws CreateTableBuilderException {
        if (column == null)
            throw new CreateTableBuilderException("Column can not be null.");

        if (this.primaryKeyName != null && Objects.equals(column.getName(), this.primaryKeyName))
            throw new CreateTableBuilderException("Column name '" + column.getName() + "' already exists.");

        if (this.columns.stream().anyMatch(col -> Objects.equals(col.getName(), column.getName())))
            throw new CreateTableBuilderException("Column name '" + column.getName() + "' already exists.");

        this.columns.add(column);
        return this;
    }

    public String build() throws CreateTableBuilderException {
        if (this.columns.isEmpty())
            throw new CreateTableBuilderException("Can not create a table with no columns.");

        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("CREATE TABLE ");

        if (this.checkIfNotExists)
            tableBuilder.append("IF NOT EXISTS ");

        tableBuilder.append(this.name).append(" ( ");

        // TODO: this likely depends on SQL dialect
        if (this.primaryKeyName != null) {
            tableBuilder.append(this.primaryKeyName).append(' ');
            tableBuilder.append("SERIAL PRIMARY KEY");
            if (!this.columns.isEmpty())
                tableBuilder.append(", ");
        }

        for (int i = 0; i < this.columns.size(); i++) {
            TableColumn tableColumn = this.columns.get(i);
            tableBuilder.append(tableColumn.toString());

            if (i < this.columns.size() - 1)
                tableBuilder.append(", ");
        }

        tableBuilder.append(");");
        return tableBuilder.toString();
    }
}
