package zweaver.sqlbuilder;

import zweaver.sqlbuilder.enums.EFilterConjunction;

public class FilterCondition {
    private String condition;
    private EFilterConjunction conjunction;

    public FilterCondition(String condition, EFilterConjunction conjunction) {
        this.condition = condition;
        this.conjunction = conjunction;
    }

    public EFilterConjunction getConjunction() {
        return this.conjunction;
    }

    public void setConjunction(EFilterConjunction conjunction) {
        this.conjunction = conjunction;
    }

    public String toString() {
        StringBuilder conditionBuilder = new StringBuilder().append(this.condition);
        if (this.conjunction != EFilterConjunction.NONE) {
            conditionBuilder.append(' ');
            switch (this.conjunction) {
                case EFilterConjunction.AND -> conditionBuilder.append("AND");
                case EFilterConjunction.OR -> conditionBuilder.append("OR");
            }
            conditionBuilder.append(' ');
        }

        return conditionBuilder.toString();
    }
}
