package zweaver.sqlbuilder.builders;

import zweaver.sqlbuilder.enums.EFilterCondition;
import zweaver.sqlbuilder.enums.EFilterGroupType;
import zweaver.sqlbuilder.util.SelectUtil;

import java.util.ArrayList;
import java.util.List;

public class FilterGroupBuilder {
    private final List<String> filterConditions;
    private EFilterGroupType groupType;

    public FilterGroupBuilder() {
        this.filterConditions = new ArrayList<>();
        this.groupType = EFilterGroupType.ALL;
    }

    public <T> FilterGroupBuilder addFilter(String columnName, EFilterCondition condition, T value, boolean valueIsQuoted) {
        this.filterConditions.add(SelectUtil.buildFilterString(columnName, condition, value, valueIsQuoted));
        return this;
    }

    public <T> FilterGroupBuilder addFilterWithAlias(String columnName, String alias, EFilterCondition condition, T value, boolean valueIsQuoted) {
        String newColumnName = SelectUtil.buildFilterAlias(columnName, alias);
        return this.addFilter(newColumnName, condition, value, valueIsQuoted);
    }

    public FilterGroupBuilder allOf() {
        this.groupType = EFilterGroupType.ALL;
        return this;
    }

    public FilterGroupBuilder anyOf() {
        this.groupType = EFilterGroupType.ANY;
        return this;
    }

    public List<String> getFilterConditions() {
        return this.filterConditions;
    }

    public EFilterGroupType getGroupType() {
        return this.groupType;
    }
}
