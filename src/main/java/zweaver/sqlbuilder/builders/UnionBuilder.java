package zweaver.sqlbuilder.builders;

import zweaver.sqlbuilder.enums.EUnionType;
import zweaver.sqlbuilder.exceptions.SelectBuilderException;
import zweaver.sqlbuilder.exceptions.UnionBuilderException;

import java.util.ArrayList;
import java.util.List;

public final class UnionBuilder {
    private final List<SelectBuilder> queries;
    private final List<EUnionType> unionTypes;

    public UnionBuilder(SelectBuilder initialQuery) throws UnionBuilderException {
        if (initialQuery == null)
            throw new UnionBuilderException("Query can not be null.");

        this.queries = new ArrayList<>();
        this.queries.add(initialQuery);

        this.unionTypes = new ArrayList<>();
    }

    public UnionBuilder union(SelectBuilder query) throws UnionBuilderException {
        return this.unionGeneric(query, EUnionType.UNION);
    }

    public UnionBuilder unionAll(SelectBuilder query) throws UnionBuilderException {
        return this.unionGeneric(query, EUnionType.UNION_ALL);
    }

    public String build() throws SelectBuilderException {
        StringBuilder unionBuilder = new StringBuilder();
        unionBuilder.append(this.queries.getFirst().build(false));
        for (int i = 1; i < this.queries.size(); i++) {
            unionBuilder.append(' ');
            switch (this.unionTypes.get(i - 1)) {
                case UNION -> unionBuilder.append("UNION");
                case UNION_ALL -> unionBuilder.append("UNION ALL");
            }
            unionBuilder.append(' ');
            if (i < this.queries.size() - 1)
                unionBuilder.append(this.queries.get(i).build(false));
            else
                unionBuilder.append(this.queries.get(i).build(true));
        }


        return unionBuilder.toString();
    }

    private UnionBuilder unionGeneric(SelectBuilder query, EUnionType unionType) throws UnionBuilderException {
        if (query == null)
            throw new UnionBuilderException("Query can not be null.");

        this.queries.add(query);
        this.unionTypes.add(unionType);
        return this;
    }
}
