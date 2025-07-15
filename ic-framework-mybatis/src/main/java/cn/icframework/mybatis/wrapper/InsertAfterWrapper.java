package cn.icframework.mybatis.wrapper;

import cn.icframework.common.lambda.LambdaGetter;
import cn.icframework.common.lambda.LambdaUtils;
import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.utils.ModelClassUtils;
import jakarta.annotation.Nonnull;

import java.lang.reflect.Field;
import java.util.Map;

public class InsertAfterWrapper extends SqlWrapper {
    SqlWrapper sqlWrapper;

    public InsertAfterWrapper(SqlWrapper sqlWrapper) {
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
    public InsertAfterColumnsWrapper COLUMNS(QueryField<?>... queryFields) {
        sqlWrapper.columns.clear();
        for (QueryField<?> queryField : queryFields) {
            sqlWrapper.columns.add(queryField.getTableColumn());
        }
        return new InsertAfterColumnsWrapper(sqlWrapper);
    }

    /**
     * INTO
     * SELECT
     *
     * @return
     */
    @SafeVarargs
    public final <T> InsertAfterColumnsWrapper COLUMNS(LambdaGetter<T>... lambdaGetters) {
        sqlWrapper.columns.clear();
        for (LambdaGetter<?> lambdaGetter : lambdaGetters) {
            Field field = LambdaUtils.getField(lambdaGetter);
            String tableColumnName = ModelClassUtils.getTableColumnName(field);
            if (tableColumnName == null) {
                throw new RuntimeException("字段 " + field.getName() + " 没有 @TableField 或 @Id 注解");
            }
            sqlWrapper.columns.add(tableColumnName);
        }
        return new InsertAfterColumnsWrapper(sqlWrapper);
    }

}
