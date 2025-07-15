package cn.icframework.mybatis.wrapper;

import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.consts.StatementType;
import cn.icframework.mybatis.consts.CompareEnum;
import cn.icframework.mybatis.utils.ModelClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

public class SqlEntityWrapperHelper {
    public static void fromEntity(SqlWrapper sqlWrapper, Class<?>[] entities) {
        boolean handleSelect = sqlWrapper.statementType == StatementType.SELECT && sqlWrapper.select.isEmpty();
        for (Class<?> entity : entities) {
            String tableName = ModelClassUtils.getTableName(entity);
            sqlWrapper.tables.add(tableName);
            logicDelNe(sqlWrapper, entity);
            if (handleSelect) {
                select(sqlWrapper, tableName, entity);
            }
        }
    }

    private static void select(SqlWrapper sqlWrapper, String tableName, Class<?> entity) {
        Table table = entity.getAnnotation(Table.class);
        ModelClassUtils.handleTableFieldInfo(entity, field -> {
            String fieldName = ModelClassUtils.getTableColumnName(table, field.getField());
            if (StringUtils.hasLength(fieldName)) {
                sqlWrapper.select.add(String.format("%s.%s", tableName, fieldName));
            }
            return true;
        });
    }


    /**
     * 根据表实体
     *
     * @param entity
     */
    private static void logicDelNe(SqlWrapper sqlWrapper, Class<?> entity) {
        Table table = entity.getAnnotation(Table.class);
        Field logicDeleteField = ModelClassUtils.getLogicDelete(entity);
        if (logicDeleteField != null) {
            sqlWrapper.whereLogicDel.add(String.format("%s.%s%s%s", ModelClassUtils.getTableName(entity), ModelClassUtils.getTableColumnName(table, logicDeleteField), CompareEnum.NE.getKey(sqlWrapper.isCoverXml()), IcParamsConsts.PARAMETER_LOGIC_DELETE_GET));
        }
    }
}
