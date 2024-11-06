package zweaver.sqlbuilder.util;

import zweaver.sqlbuilder.enums.EFilterCondition;

public class SelectUtil {

    /**
     * Sanitize string by doubling up isolated single quotes to reduce risk of SQL injection.
     * E.g., "My st'ring with q''uotes" --> "My st''ring with q''uotes"
     * @param value The string to sanitize
     * @return The sanitized string
     */
    public static String sanitizeQuotes(String value) {
        StringBuilder buffer = new StringBuilder();

        if (value.contains("'")) {
            for (int i = 0; i < value.length(); i++) {
                // single quote, not at end of string
                if (value.charAt(i) == '\'' && i < value.length() - 1) {
                    // if next character is NOT single quote, double it up
                    if (value.charAt(i + 1) != '\'')
                        buffer.append("''");
                        // if next character IS single quote, append as is and skip over
                    else if (value.charAt(i + 1) == '\'') {
                        buffer.append("''");
                        i++;
                    }
                    // otherwise, append as normal
                    else
                        buffer.append(value.charAt(i));
                }
                // if single quote at end of string, double it up
                else if (value.charAt(i) == '\'' && i == value.length() - 1)
                    buffer.append("''");
                    // anything else, append as normal
                else
                    buffer.append(value.charAt(i));
            }

            return buffer.toString();
        }
        else
            return value;
    }

    public static <T> String buildFilterString(String columnName, EFilterCondition condition, T value, boolean valueIsQuoted) {
        StringBuilder filterBuilder = new StringBuilder();
        filterBuilder.append(columnName);
        filterBuilder.append(' ');
        switch (condition) {
            case EFilterCondition.EQUAL -> filterBuilder.append('=');
            case EFilterCondition.NOT_EQUAL -> filterBuilder.append("!=");
            case EFilterCondition.GREATER_THAN -> filterBuilder.append('>');
            case EFilterCondition.GREATER_THAN_EQUAL -> filterBuilder.append(">=");
            case EFilterCondition.LESS_THAN -> filterBuilder.append('<');
            case EFilterCondition.LESS_THAN_EQUAL -> filterBuilder.append("<=");
            case EFilterCondition.LIKE -> filterBuilder.append("LIKE");
            case EFilterCondition.ILIKE -> filterBuilder.append("ILIKE");
            case EFilterCondition.IN -> filterBuilder.append("IN");
            case EFilterCondition.NOT_IN -> filterBuilder.append("NOT IN");
        }
        filterBuilder.append(' ');

        if (condition == EFilterCondition.IN || condition == EFilterCondition.NOT_IN) {
            filterBuilder.append('(');
            if (value instanceof Iterable<?>) {
                Iterable<?> itemList = (Iterable<?>) value;
                for (Object item : itemList) {
                    if (valueIsQuoted)
                        filterBuilder.append("'").append(SelectUtil.sanitizeQuotes(String.valueOf(item))).append("'");
                    else
                        filterBuilder.append(item);
                    filterBuilder.append(',');
                }
                filterBuilder.setCharAt(filterBuilder.length() - 1, ')');
            } else {
                if (valueIsQuoted)
                    filterBuilder.append("'").append(SelectUtil.sanitizeQuotes(String.valueOf(value))).append("'");
                else
                    filterBuilder.append(value);
                filterBuilder.append(')');
            }
        } else {
            if (valueIsQuoted)
                filterBuilder.append("'").append(SelectUtil.sanitizeQuotes(String.valueOf(value))).append("'");
            else
                filterBuilder.append(value);
        }

        return filterBuilder.toString();
    }

    public static String buildFilterAlias(String columnName, String alias) {
        return new StringBuilder().append(alias).append('.').append(columnName).toString();
    }
}
