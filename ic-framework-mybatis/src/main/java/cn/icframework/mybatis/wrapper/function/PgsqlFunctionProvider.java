package cn.icframework.mybatis.wrapper.function;

import cn.icframework.mybatis.query.QueryField;

public class PgsqlFunctionProvider implements SqlFunctionProvider {
    public QueryField<?> max(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("MAX(%s)", field));
        return queryField;
    }
    public QueryField<?> min(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("MIN(%s)", field));
        return queryField;
    }
    public QueryField<?> avg(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("AVG(%s)", field));
        return queryField;
    }
    public QueryField<?> sum(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("SUM(%s)", field));
        return queryField;
    }
    public QueryField<?> lower(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("LOWER(%s)", field));
        return queryField;
    }
    public QueryField<?> upper(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("UPPER(%s)", field));
        return queryField;
    }
    public QueryField<?> length(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("LENGTH(%s)", field));
        return queryField;
    }
    public QueryField<?> substring(String field, int pos, int len) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("SUBSTRING(%s FROM %d FOR %d)", field, pos, len));
        return queryField;
    }
    public QueryField<?> trim(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("TRIM(%s)", field));
        return queryField;
    }
    public QueryField<?> round(String field, int scale) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("ROUND(%s, %d)", field, scale));
        return queryField;
    }
    public QueryField<?> ceil(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("CEIL(%s)", field));
        return queryField;
    }
    public QueryField<?> floor(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("FLOOR(%s)", field));
        return queryField;
    }
    public QueryField<?> abs(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("ABS(%s)", field));
        return queryField;
    }
    public QueryField<?> now() {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc("NOW()");
        return queryField;
    }
    public QueryField<?> dateFormat(String field, String format) {
        QueryField<?> queryField = new QueryField<>();
        // PostgreSQL 用 TO_CHAR 进行格式化
        queryField.setFunc(String.format("TO_CHAR(%s, '%s')", field, format));
        return queryField;
    }
    public QueryField<?> year(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("EXTRACT(YEAR FROM %s)", field));
        return queryField;
    }
    public QueryField<?> month(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("EXTRACT(MONTH FROM %s)", field));
        return queryField;
    }
    public QueryField<?> day(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("EXTRACT(DAY FROM %s)", field));
        return queryField;
    }
    public QueryField<?> count(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("COUNT(%s)", field));
        return queryField;
    }
    public QueryField<?> count() {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc("COUNT(1)");
        return queryField;
    }
    public QueryField<?> anyValue(String field) {
        // PostgreSQL 没有 ANY_VALUE，可以用 MIN(field) 或 MAX(field) 替代
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("MIN(%s)", field));
        return queryField;
    }
    public QueryField<?> concat(Object... vals) {
        // PostgreSQL 字符串拼接用 ||
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            if (i > 0) sb.append(" || ");
            Object val = vals[i];
            if (val instanceof QueryField<?>) {
                sb.append(((QueryField<?>) val).getNameWithTable());
            } else if (val instanceof String) {
                sb.append("'").append(val).append("'");
            } else {
                sb.append(val.toString());
            }
        }
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(sb.toString());
        return queryField;
    }

    @Override
    public QueryField<?> distinct(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("DISTINCT %s", field));
        return queryField;
    }
} 