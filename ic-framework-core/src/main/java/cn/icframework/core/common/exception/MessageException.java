package cn.icframework.core.common.exception;

/**
 * 业务自定义异常，对外输出信息。
 * <p>
 * 用于封装业务异常信息，可携带原始异常。
 * </p>
 * @author hzl
 * @since 2021-06-17  18:24:00
 */
public class MessageException extends RuntimeException{
    /**
     * 原始异常
     */
    private Exception exception;

    /**
     * 构造方法，仅携带消息
     * @param message 异常信息
     */
    public MessageException(String message) {
        super(message);
    }

    /**
     * 构造方法，携带消息和原始异常
     * @param message 异常信息
     * @param e 原始异常
     */
    public MessageException(String message, Exception e) {
        super(message);
        exception = e;
    }

    /**
     * 获取原始异常
     * @return Exception
     */
    public Exception getException() {
        if (exception == null){
            return this;
        }
        return exception;
    }

    /**
     * 设置原始异常
     * @param exception 原始异常
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
