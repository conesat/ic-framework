package cn.icframework.mybatis.wrapper.function;

import cn.icframework.mybatis.query.QueryField;

public interface SqlFunctionProvider {
    QueryField<?> max(String field);

    QueryField<?> min(String field);

    QueryField<?> avg(String field);

    QueryField<?> sum(String field);

    QueryField<?> lower(String field);

    QueryField<?> upper(String field);

    QueryField<?> length(String field);

    QueryField<?> substring(String field, int pos, int len);

    QueryField<?> trim(String field);

    QueryField<?> round(String field, int scale);

    QueryField<?> ceil(String field);

    QueryField<?> floor(String field);

    QueryField<?> abs(String field);

    QueryField<?> now();

    QueryField<?> dateFormat(String field, String format);

    QueryField<?> year(String field);

    QueryField<?> month(String field);

    QueryField<?> day(String field);

    QueryField<?> count(String field);

    QueryField<?> count();

    QueryField<?> anyValue(String field);

    QueryField<?> concat(Object... vals);
}
