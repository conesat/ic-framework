package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.consts.StatementType;
import jakarta.annotation.Nonnull;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertAfterColumnsWrapper extends SqlWrapper {
    SqlWrapper sqlWrapper;

    public InsertAfterColumnsWrapper(SqlWrapper sqlWrapper) {
        this.sqlWrapper = sqlWrapper;
    }

    /**
     * 获取插入sql
     *
     * @return sql
     */
    public String sql() {
        return sqlWrapper.sql();
    }

    /**
     * 获取入参
     *
     * @return 参数map
     */
    public Map<String, Object> getParams() {
        return sqlWrapper.getParams();
    }

    /**
     * INTO
     * SELECT
     *
     * @return
     */
    public SqlWrapper VALUES(@Nonnull SqlWrapper selectSqlWrapper) {
        if (selectSqlWrapper.statementType != StatementType.SELECT) {
            throw new RuntimeException("INSERT 嵌套查询只能是 SELECT 类型");
        }
        sqlWrapper.insertSelect(selectSqlWrapper);
        return sqlWrapper;
    }

    /**
     * INTO
     * SELECT
     *
     * @return
     */
    public InsertAfterColumnsWrapper VALUES(List<Object> values) {
        for (Object value : values) {
            VALUES(value);
        }
        return this;
    }

    public InsertAfterColumnsWrapper VALUES(Object value) {
        Class<?> aClass = value.getClass();
        List<String> vs = new ArrayList<>();
        for (String column : columns) {
            Method method = null;
            try {
                method = aClass.getMethod("get" + StringUtils.capitalize(column));
            } catch (NoSuchMethodException ignored) {
            }
            if (method == null) {
                try {
                    method = aClass.getMethod("is" + StringUtils.capitalize(column));
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (method == null) {
                continue;
            }
            try {
                Object val = method.invoke(value);
                vs.add(sqlWrapper.putParamSync(val));
            } catch (IllegalAccessException | InvocationTargetException ignored) {
                vs.add("null");
            }
        }
        sqlWrapper.valuesList.add(vs);
        return this;
    }

    public InsertAfterColumnsWrapper VALUES(Object... vals) {
        List<String> vs = new ArrayList<>();
        for (Object val : vals) {
            vs.add(sqlWrapper.putParamSync(val));
        }
        sqlWrapper.valuesList.add(vs);
        return this;
    }
}
