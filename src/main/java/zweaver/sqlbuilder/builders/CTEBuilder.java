package zweaver.sqlbuilder.builders;

import zweaver.sqlbuilder.exceptions.CTEBuilderException;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;

import java.util.ArrayList;
import java.util.List;

public final class CTEBuilder {
    private final List<String> names;
    private final List<SelectBuilder> selectQueries;
    private SelectBuilder mainQuery;

    public CTEBuilder() {
        this.names = new ArrayList<>();
        this.selectQueries = new ArrayList<>();
        this.mainQuery = null;
    }

    public CTEBuilder addTable(String name, SelectBuilder selectQuery) throws CTEBuilderException {
        if (name == null || name.isEmpty())
            throw new CTEBuilderException("CTE name can not be empty.");

        if (this.names.contains(name))
            throw new CTEBuilderException("CTE with name '" + name + "' already exists.");

        if (selectQuery == null)
            throw new CTEBuilderException("CTE select query can not be null.");

        this.names.add(name);
        this.selectQueries.add(selectQuery);
        return this;
    }

    public CTEBuilder setQuery(SelectBuilder selectBuilder) throws CTEBuilderException {
        if (selectBuilder == null)
            throw new CTEBuilderException("Main query can not be null.");

        this.mainQuery = selectBuilder;
        return this;
    }

    public String build() throws CTEBuilderException, SelectBuilderException {
        if (this.names.isEmpty())
            throw new CTEBuilderException("CTE does not have any tables.");

        if (this.mainQuery == null)
            throw new CTEBuilderException("CTE is missing a main query.");

        if (!this.names.contains(this.mainQuery.getTableName()))
            throw new CTEBuilderException("Main query selects from non-existing table in CTE.");

        StringBuilder cteBuilder = new StringBuilder();

        for (int i = 0; i < this.names.size(); i++) {
            if (i == 0)
                cteBuilder.append("WITH ");
            cteBuilder.append(this.names.get(i)).append(" AS ( ");
            cteBuilder.append(this.selectQueries.get(i).build(false)).append(" )");
            if (i < this.names.size() - 1)
                cteBuilder.append(", ");
        }

        cteBuilder.append(' ').append(this.mainQuery.build(true));
        return cteBuilder.toString();
    }
}
