package cn.icframework.mybatis.query;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;

/**
 * QueryField并发测试类，用于测试并发clone操作是否会出现ConcurrentModificationException
 */
public class QueryFieldConcurrentTest {

    // 简单的User实体类用于测试
    @Getter
    @Setter
    public static class User {
        private Long id;
        private String name;
        private Integer age;
        private String email;
    }

    // 自定义查询表类，继承QueryTable
    public static class UserDef extends QueryTable<UserDef> {
        public UserDef() {
            super(User.class);
        }

        // 添加带Class参数的构造函数，用于newInstance方法调用
        public UserDef(Class<?> tableClass) {
            super(tableClass);
        }

        // 静态工厂方法，用于创建实例
        public static UserDef table() {
            return new UserDef();
        }

        // 表字段对应的QueryField对象
        public QueryField<UserDef> id = new QueryField<>(this, "id");
        public QueryField<UserDef> name = new QueryField<>(this, "name");
        public QueryField<UserDef> age = new QueryField<>(this, "age");
        public QueryField<UserDef> email = new QueryField<>(this, "email");
    }

    /**
     * 测试并发环境下调用eq方法（内部会调用cloneQt）是否会出现ConcurrentModificationException
     */
    @Test
    public void testConcurrentCloneQt() throws InterruptedException {
        // 创建线程池
        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        // 创建测试用的UserDef实例
        UserDef userDef = UserDef.table();

        // 并发执行eq方法调用
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    // 循环调用eq方法，增加并发冲突的可能性
                    for (int j = 0; j < 1000; j++) {
                        final int finalJ = j;
                        assertDoesNotThrow(() -> {
                            // 模拟调用eq方法，内部会调用cloneQt
                            userDef.name.eq("test" + finalJ);
                        });
                    }
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        // 等待所有线程执行完成
        countDownLatch.await();
        executorService.shutdown();

        // 验证没有出现异常
        assert exceptionCount.get() == 0 : "并发测试中出现了" + exceptionCount.get() + "个异常";
    }

    /**
     * 测试多种条件方法的并发调用
     */
    @Test
    public void testConcurrentMultiMethods() throws InterruptedException {
        // 创建线程池
        int threadCount = 15;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        // 创建测试用的UserDef实例
        UserDef userDef = UserDef.table();

        // 并发执行多种条件方法调用
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                try {
                    // 循环调用不同的条件方法
                    for (int j = 0; j < 500; j++) {
                        final int finalJ = j;
                        int methodIndex = (j + threadIndex) % 5;
                        final int finalMethodIndex = methodIndex;
                        assertDoesNotThrow(() -> {
                            // 交替调用不同的条件方法
                            switch (finalMethodIndex) {
                                case 0:
                                    userDef.name.eq("test" + finalJ);
                                    break;
                                case 1:
                                    userDef.name.like("%test%" + finalJ);
                                    break;
                                case 2:
                                    userDef.age.gt(finalJ);
                                    break;
                                case 3:
                                    userDef.id.in(new Object[]{finalJ, finalJ + 1, finalJ + 2});
                                    break;
                                case 4:
                                    userDef.name.asc();
                                    break;
                            }
                        });
                    }
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        // 等待所有线程执行完成
        countDownLatch.await();
        executorService.shutdown();

        // 验证没有出现异常
        assert exceptionCount.get() == 0 : "并发测试中出现了" + exceptionCount.get() + "个异常";
    }
}