package org.apache.ibatis.session;

import cn.icframework.mybatis.builder.StatementBuilder;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.ParameterizedType;

/**
 * 覆盖mybatis的默认插入方法
 * 重新根据实体构建插入方法
 * @author hzl
 * @since 2023/5/31
 */
public class MappedStatementBuilder {
    private static final String[] insertMethods = new String[]{"insert", "insertBatch"};

    public MappedStatementBuilder() {
    }

    /**
     * 只有继承 BasicMapper 才会进入这里
     * 用同样的类型，同样的方法名，覆盖默认mybatis的方法
     * @param configuration
     * @param dao mapper文件
     */
    public static void rebuild(Configuration configuration, Class<?> dao, int datacenterId, int workerId) {
        ParameterizedType type = (ParameterizedType) dao.getGenericInterfaces()[0];
        Class<?> entityType = (Class<?>) type.getActualTypeArguments()[0];
        // 如果当前执行的是 insert 或者 insertBatch，重构插入语句
        for (String queryMethod : insertMethods) {
            rebuildInsert(configuration, mapStatementID(dao, queryMethod), entityType, datacenterId, workerId);
        }
    }

    private static String mapStatementID(Class<?> daoType, String method) {
        return daoType.getName() + "." + method;
    }


    private static void rebuildInsert(Configuration configuration, String id, Class<?> type, int datacenterId, int workerId) {
        try {
            MappedStatement statement = configuration.mappedStatements.get(id);
            if (statement != null) {
                // 首先需要移除掉mybatis创建的 MappedStatement，
                // 然后用IC的生成一个重新放回 configuration，
                // 这样下一步执行的sql就是我们创建好的了
                configuration.mappedStatements.remove(id);
                MappedStatement ms = (new StatementBuilder(statement)).rebuildInsert(type, datacenterId, workerId);
                configuration.mappedStatements.put(id, ms);
            }
        } catch (Exception ignored) {
        }
    }
}
