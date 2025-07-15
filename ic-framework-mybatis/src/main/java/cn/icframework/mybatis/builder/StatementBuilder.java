package cn.icframework.mybatis.builder;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.consts.IdType;
import cn.icframework.mybatis.utils.ModelClassUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Field;

/**
 * SQL语句构建器。
 * 用于构建各种类型的SQL语句。
 * @author hzl
 * @since 2023/5/31
 */

public class StatementBuilder extends MappedStatement.Builder {
    private final MappedStatement statement;

    public StatementBuilder(MappedStatement statement) {
        super(statement.getConfiguration(), statement.getId(), statement.getSqlSource(), statement.getSqlCommandType());
        this.statement = statement;
    }

    /**
     * 重构插入方法
     *
     * @param type
     * @return
     */
    public MappedStatement rebuildInsert(Class<?> type, int datacenterId, int workerId) {
        Table table = type.getAnnotation(Table.class);
        // 货权当前实体
        Field field = ModelClassUtils.getIdField(type);
        Id id = null;
        if (field != null) {
            id = field.getDeclaredAnnotation(Id.class);
        }
        if (id == null) {
            return this.statement;
        }
        if (id.idType() == IdType.INPUT) {
            return this.statement;
        } else {
            String fieldName = ModelClassUtils.getTableColumnName(table, field);
            this.keyColumn(fieldName);
            this.keyProperty(IcParamsConsts.PARAMETER_ENTITY + "." + fieldName);
            if (id.idType() == IdType.AUTO) {
                this.keyGenerator(Jdbc3KeyGenerator.INSTANCE);
            } else if (id.idType() == IdType.UUID) {
                this.keyGenerator(Generators.UUID_GENERATOR);
            } else if (id.idType() == IdType.SNOWFLAKE) {
                this.keyGenerator(Generators.SNOW_GENERATOR(datacenterId, workerId));
            }
            return this.build();
        }
    }
}