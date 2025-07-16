package cn.icframework.mybatis.wrapper.function;

import cn.icframework.mybatis.query.QueryField;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.List;

public class MysqlFunctionProvider implements SqlFunctionProvider {
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
        queryField.setFunc(String.format("SUBSTRING(%s, %d, %d)", field, pos, len));
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
        queryField.setFunc(String.format("DATE_FORMAT(%s, '%s')", field, format));
        return queryField;
    }
    public QueryField<?> year(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("YEAR(%s)", field));
        return queryField;
    }
    public QueryField<?> month(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("MONTH(%s)", field));
        return queryField;
    }
    public QueryField<?> day(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("DAY(%s)", field));
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
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("ANY_VALUE(%s)", field));
        return queryField;
    }
    public QueryField<?> concat(Object... vals) {
        Assert.notNull(vals, "concat 参数必填");
        List<String> concatList = new ArrayList<>();
        for (Object val : vals) {
            if (val instanceof QueryField<?>) {
                concatList.add(((QueryField<?>) val).getNameWithTable());
            } else if (val instanceof String) {
                concatList.add(String.format("'%s'", val));
            } else {
                concatList.add(val.toString());
            }
        }
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc("CONCAT(" + String.join(",", concatList) + ")");
        return queryField;
    }

    @Override
    public QueryField<?> distinct(String field) {
        QueryField<?> queryField = new QueryField<>();
        queryField.setFunc(String.format("DISTINCT %s", field));
        queryField.setAsName(field);
        return queryField;
    }
} 