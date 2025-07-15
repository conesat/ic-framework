package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.query.QueryTable;
import cn.icframework.mybatis.utils.ModelClassUtils;

import java.lang.reflect.Field;

public class DeleteWrapper {

    /**
     * FROM
     *
     * @param queryTable
     * @return
     */
    public SqlWrapper FROM(QueryTable<?> queryTable) {
        Field logicDeleteField = ModelClassUtils.getLogicDelete(queryTable.getTableClass());
        if (logicDeleteField != null) {
            UpdateWrapper updateWrapper = new UpdateWrapper(queryTable);
            updateWrapper.setLogicDel(ModelClassUtils.getTableColumnName(logicDeleteField));
            return updateWrapper;
        }
        SqlWrapper sqlWrapper = new SqlWrapper(StatementType.DELETE);
        sqlWrapper.from(queryTable);
        return sqlWrapper;
    }

    /**
     * FROM
     *
     * @param entity
     * @param coverXml 是否需要xml转义
     * @return
     */
    public SqlWrapper FROM(boolean coverXml, Class<?> entity) {
        Field logicDeleteField = ModelClassUtils.getLogicDelete(entity);
        if (logicDeleteField != null) {
            UpdateWrapper updateWrapper = new UpdateWrapper(entity);
            updateWrapper.setCoverXml(coverXml);
            updateWrapper.setLogicDel(ModelClassUtils.getTableColumnName(logicDeleteField));
            return updateWrapper;
        }
        SqlWrapper sqlWrapper = new SqlWrapper(StatementType.DELETE);
        sqlWrapper.setCoverXml(coverXml);
        sqlWrapper.fromEntity(entity);
        return sqlWrapper;
    }

}
