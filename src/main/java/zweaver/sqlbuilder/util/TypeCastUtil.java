package zweaver.sqlbuilder.util;

import zweaver.sqlbuilder.SQLContext;

import java.util.List;

public class TypeCastUtil {
    public static String castTo(SQLContext context, String columnName, String typeName, List<String> typeArgs) {
        if (typeArgs == null || typeArgs.isEmpty())
            return TypeCastUtil.buildTypeCastWithNoArgs(context, columnName, typeName);
        else
            return TypeCastUtil.buildTypeCastWithArgs(context, columnName, typeName, typeArgs);
    }

    private static String buildTypeCastWithNoArgs(SQLContext context, String columnName, String typeName) {
        return switch(context.getSqlDialect()) {
            case POSTGRES, VERTICA -> new StringBuilder()
                    .append(columnName)
                    .append("::")
                    .append(typeName)
                    .toString();
            default -> new StringBuilder()
                    .append("CAST(")
                    .append(columnName)
                    .append(" AS ")
                    .append(typeName)
                    .append(')')
                    .toString();
        };
    }

    private static String buildTypeCastWithArgs(SQLContext context, String columnName, String typeName, List<String> typeArgs) {
        String argumentList = TypeCastUtil.buildArgumentList(typeArgs);

        return switch(context.getSqlDialect()) {
            case POSTGRES, VERTICA -> new StringBuilder()
                    .append(columnName)
                    .append("::")
                    .append(typeName)
                    .append('(')
                    .append(argumentList)
                    .append(')')
                    .toString();
            default -> new StringBuilder()
                    .append("CAST(")
                    .append(columnName)
                    .append(" AS ")
                    .append(typeName)
                    .append('(')
                    .append(argumentList)
                    .append("))")
                    .toString();
        };
    }

    private static String buildArgumentList(List<String> typeArgs) {
        StringBuilder argList = new StringBuilder();
        for (int i = 0; i < typeArgs.size(); i++) {
            argList.append(typeArgs.get(i));
            if (i < typeArgs.size() - 1)
                argList.append(", ");
        }
        return argList.toString();
    }
}
