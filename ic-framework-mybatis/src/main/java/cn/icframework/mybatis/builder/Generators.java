package cn.icframework.mybatis.builder;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Generators {
    private static final Lock lock = new ReentrantLock();
    private static SnowFlakeGenerator snowFlakeGenerator;
    public static final UUIDKeyGenerator UUID_GENERATOR = new UUIDKeyGenerator();

    public static SnowFlakeGenerator SNOW_GENERATOR(int datacenterId, int workerId) {
        if (snowFlakeGenerator == null) {
            lock.lock();
            try {
                snowFlakeGenerator = new SnowFlakeGenerator(datacenterId, workerId);
            } finally {
                lock.unlock();
            }
        }
        return snowFlakeGenerator;
    }
}
