package cn.icframework.mybatis.builder;

import cn.icframework.mybatis.consts.IcParamsConsts;
import cn.icframework.mybatis.utils.ModelClassUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class IdKeyGenerator implements KeyGenerator {

    abstract Object generate();

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        Object o = ((Map<?, ?>) parameter).get(IcParamsConsts.PARAMETER_ENTITY);
        List<?> entities;
        if (o instanceof List<?>) {
            entities = ((List<?>) o);
        } else {
            entities = Collections.singletonList(o);
        }
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        // 暂不支持多主键 只用了第一个key
        Field fieldSet = ModelClassUtils.getDeclaredField(entities.getFirst().getClass(), ms.getKeyColumns()[0]);
        if (fieldSet != null) {
            for (Object entity : entities) {
                try {
                    fieldSet.setAccessible(true);
                    fieldSet.set(entity, generate());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }
}