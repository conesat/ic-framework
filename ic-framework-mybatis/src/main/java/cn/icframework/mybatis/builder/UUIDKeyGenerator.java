package cn.icframework.mybatis.builder;

import java.util.UUID;

/**
 * uuid生成器
 */
public class UUIDKeyGenerator extends IdKeyGenerator {


    @Override
    public Object generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
