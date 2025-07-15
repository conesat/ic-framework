package cn.icframework.mybatis.config;

import cn.icframework.mybatis.mapper.BasicMapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.MappedStatementBuilder;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * IC框架 MyBatis 配置类
 * 
 * 该类负责在 MyBatis 初始化后对 Mapper 接口进行后处理，
 * 主要功能包括：
 * 1. 通过反射获取 MapperProxy 的内部字段
 * 2. 对 BasicMapper 类型的 Bean 进行后处理
 * 3. 重建 MappedStatement，注入雪花算法的工作节点信息
 * 
 * @author hzl
 * @since 2023/5/31
 */
@Configuration
@AutoConfigureAfter({MybatisAutoConfiguration.class})
public class IcMyBatisConfig implements BeanPostProcessor {
    
    /**
     * MapperProxy 类中的 sqlSession 字段
     * 用于通过反射获取 SqlSession 实例
     */
    private final Field sqlSessionField = ReflectionUtils.findField(MapperProxy.class, (String) null, SqlSession.class);
    
    /**
     * MapperProxy 类中的 mapperInterface 字段
     * 用于通过反射获取 Mapper 接口类
     */
    private final Field mapperInterfaceField = ReflectionUtils.findField(MapperProxy.class, "mapperInterface");

    /**
     * IC框架配置对象
     * 用于获取雪花算法的数据中心ID和工作节点ID
     */
    @Resource
    private SnowflakeConfig snowflakeConfig;

    /**
     * 构造函数
     * 初始化反射字段，设置字段可访问性
     */
    public IcMyBatisConfig() {
        // 设置 sqlSession 字段可访问
        (Objects.requireNonNull(this.sqlSessionField, "MapperProxy sqlSession NOT FOUND")).setAccessible(true);
        // 设置 mapperInterface 字段可访问
        (Objects.requireNonNull(this.mapperInterfaceField, "MapperProxy mapperInterface NOT FOUND")).setAccessible(true);
    }

    /**
     * Bean 后处理方法
     * 
     * 在 Bean 初始化后，检查是否为 BasicMapper 类型，
     * 如果是则获取其代理处理器并进行处理
     * 
     * @param bean 待处理的 Bean 对象
     * @param beanName Bean 名称
     * @return 处理后的 Bean 对象
     * @throws BeansException Bean 处理异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, @NonNull String beanName) throws BeansException {
        // 检查 Bean 是否为 BasicMapper 类型
        if (BasicMapper.class.isAssignableFrom(bean.getClass())) {
            // 获取 Bean 的代理处理器
            InvocationHandler h = Proxy.getInvocationHandler(bean);
            // 处理 Mapper 接口
            this.processDao(h);
        }
        return bean;
    }

    /**
     * 处理 Mapper 接口
     * 
     * 通过反射获取 SqlSession 和 Mapper 接口类，
     * 然后重建 MappedStatement，注入雪花算法配置
     * 
     * @param h Mapper 的代理处理器
     */
    private void processDao(InvocationHandler h) {
        // 断言确保字段不为空
        assert this.mapperInterfaceField != null;
        assert this.sqlSessionField != null;
        
        // 通过反射获取 SqlSession 实例
        SqlSession sqlSession = Objects.requireNonNull((SqlSession) ReflectionUtils.getField(this.sqlSessionField, h));
        
        // 通过反射获取 Mapper 接口类
        Class<?> dao = Objects.requireNonNull((Class<?>) ReflectionUtils.getField(this.mapperInterfaceField, h));
        
        // 重建 MappedStatement，注入雪花算法的数据中心ID和工作节点ID
        MappedStatementBuilder.rebuild(
            sqlSession.getConfiguration(), 
            dao, 
            snowflakeConfig.getDataCenterId(),
            snowflakeConfig.getWorkerId()
        );
    }
}
