package zweaver.sqlbuilder.builders;

import zweaver.sqlbuilder.FilterCondition;
import zweaver.sqlbuilder.SQLContext;
import zweaver.sqlbuilder.enums.EFilterCondition;
import zweaver.sqlbuilder.enums.EFilterConjunction;
import zweaver.sqlbuilder.enums.EFilterGroupType;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;
import zweaver.sqlbuilder.util.SelectUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder {
    private final SQLContext context;
    private String tableName;
    private List<String> columnNames;
    private List<FilterCondition> filterConditions;
    private int limitCount;

    public SelectBuilder(SQLContext context) {
        this.context = context;
        this.tableName = null;
        this.columnNames = new ArrayList<>();
        this.filterConditions = new ArrayList<>();
        this.limitCount = 0;
    }

    public SelectBuilder fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SelectBuilder fromTableWithAlias(String tableName, String alias) {
        this.tableName = new StringBuilder().append(tableName).append(" AS ").append(alias).toString();
        return this;
    }

    public SelectBuilder selectAll() {
        this.columnNames.add("*");
        return this;
    }

    public SelectBuilder select(String columnName) {
        this.columnNames.add(columnName);
        return this;
    }

    public SelectBuilder select(List<String> columnNames) {
        this.columnNames.addAll(columnNames);
        return this;
    }

    public SelectBuilder selectWithAlias(String columnName, String alias) {
        this.columnNames.add(new StringBuilder().append(columnName).append(" AS ").append(alias).toString());
        return this;
    }

    public <T> SelectBuilder filter(String columnName, EFilterCondition condition, T value, boolean valueIsQuoted) {
        String filterCondition = SelectUtil.buildFilterString(columnName, condition, value, valueIsQuoted);
        this.addFilterCondition(filterCondition);
        return this;
    }

    public SelectBuilder filter(FilterGroupBuilder filterGroupBuilder) {
        StringBuilder filterBuilder = new StringBuilder();
        filterBuilder.append('(');
        List<String> filterBuilderConditions = filterGroupBuilder.getFilterConditions();
        for (int i = 0; i < filterBuilderConditions.size(); i++) {
            filterBuilder.append(filterBuilderConditions.get(i));
            if (i < filterBuilderConditions.size() - 1) {
                switch (filterGroupBuilder.getGroupType()) {
                    case EFilterGroupType.ALL -> filterBuilder.append(" AND ");
                    case EFilterGroupType.ANY -> filterBuilder.append(" OR ");
                }
            }
        }
        filterBuilder.append(')');
        this.addFilterCondition(filterBuilder.toString());
        return this;
    }

    public SelectBuilder limit(int limitCount) {
        this.limitCount = limitCount;
        return this;
    }

    public SelectBuilder and() {
        if (!this.filterConditions.isEmpty())
            this.filterConditions.getLast().setConjunction(EFilterConjunction.AND);

        return this;
    }

    public SelectBuilder or() {
        if (!this.filterConditions.isEmpty())
            this.filterConditions.getLast().setConjunction(EFilterConjunction.OR);

        return this;
    }

    public String build() throws SelectBuilderException {
        if (this.tableName == null || this.tableName.isEmpty())
            throw new SelectBuilderException("Table name can not be null or empty.");

        if (this.columnNames.isEmpty())
            throw new SelectBuilderException("Column names can't be empty.");

        /* SELECT */
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT").append(' ');

        /* COLUMN NAMES */
        for (int i = 0; i < this.columnNames.size(); i++) {
            queryBuilder.append(this.columnNames.get(i));
            if (i < this.columnNames.size() - 1)
                queryBuilder.append(',');
        }
        queryBuilder.append(' ');

        /* FROM */
        queryBuilder.append("FROM").append(' ');
        queryBuilder.append(this.tableName);

        /* WHERE */
        if (!this.filterConditions.isEmpty())
            queryBuilder.append(' ').append("WHERE").append(' ');

        for (FilterCondition condition : this.filterConditions)
            queryBuilder.append(condition.toString());

        /* LIMIT */
        if (this.limitCount > 0) {
            queryBuilder.append(' ');
            queryBuilder.append("LIMIT").append(' ').append(this.limitCount);
        }

        queryBuilder.append(';');
        return queryBuilder.toString();
    }

    private void addFilterCondition(String filterCondition) {
        if (this.filterConditions.isEmpty())
            this.filterConditions.add(new FilterCondition(filterCondition, EFilterConjunction.NONE));
        else {
            // if no conjunction was set, default to AND
            if (this.filterConditions.getLast().getConjunction() == EFilterConjunction.NONE)
                this.filterConditions.getLast().setConjunction(EFilterConjunction.AND);
            this.filterConditions.add(new FilterCondition(filterCondition, EFilterConjunction.NONE));
        }
    }
}
