package cn.icframework.core.common.exception;

/**
 * 需要登录异常
 *
 * 表示用户未登录时抛出的异常。
 */
public class NeedLoginException extends RuntimeException{
    /**
     * 默认构造方法
     */
    public NeedLoginException() {}
}
