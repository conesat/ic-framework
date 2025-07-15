package cn.icframework.mybatis.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MyBatis 拦截器配置类
 * 
 * <p>该类负责在 Spring 容器启动完成后，自动为所有的 SqlSessionFactory 注册分页拦截器。
 * 通过实现 ApplicationListener 接口，监听 ContextRefreshedEvent 事件，
 * 确保在 Spring 上下文完全初始化后再进行拦截器注册。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>监听 Spring 容器刷新事件</li>
 *   <li>为所有 SqlSessionFactory 实例注册分页拦截器</li>
 *   <li>确保拦截器在正确的时机被注册</li>
 * </ul>
 * 
 * @author hzl
 * @since 2023/5/23 0023
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Component
public class MyBatisInterceptorConfig implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * 分页拦截器实例
     * <p>用于处理 MyBatis 查询的分页功能，自动添加分页相关的 SQL 语句</p>
     */
    private final PageInterceptor pageInterceptor;

    /**
     * SqlSessionFactory 列表
     * <p>Spring 容器中所有的 SqlSessionFactory 实例，可能包含多个数据源的工厂</p>
     */
    private final List<SqlSessionFactory> sqlSessionFactories;

    /**
     * 处理 Spring 上下文刷新事件
     * 
     * <p>当 Spring 容器完全初始化后，该方法会被调用。
     * 此时所有的 Bean 都已经创建完成，可以安全地为 SqlSessionFactory 注册拦截器。</p>
     * 
     * <p>执行流程：</p>
     * <ol>
     *   <li>遍历所有的 SqlSessionFactory 实例</li>
     *   <li>为每个工厂的 Configuration 添加分页拦截器</li>
     *   <li>确保所有数据源都支持分页功能</li>
     * </ol>
     * 
     * @param contextRefreshedEvent Spring 上下文刷新事件，包含容器上下文信息
     */
    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        for (SqlSessionFactory factory : sqlSessionFactories) {
            factory.getConfiguration().addInterceptor(pageInterceptor);
        }
    }
}
