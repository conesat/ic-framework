package cn.icframework.core.common.bean;

import cn.icframework.core.common.bean.enums.ResponseEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用API响应实体
 * <p>
 * 封装接口返回的状态码、消息、数据等内容。
 *
 * @param <R> 数据类型
 * @author hzl
 */
@Getter
@Setter
public class Response<R> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误信息
     */
    private String err;
    /**
     * 提示消息
     */
    private String msg;
    /**
     * 响应数据
     */
    private R data;
    /**
     * 响应码
     */
    private int code;
    /**
     * 分页信息
     */
    private PageResponse<?> page;

    /**
     * 构造方法（Builder模式）
     * @param builder 构建器
     */
    private Response(Builder<R> builder) {
        this.msg = builder.msg;
        this.data = builder.data;
        this.code = builder.code;
        this.err = builder.err;
    }

    /**
     * 构建成功响应
     * @param msg 消息
     * @param data 数据
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> success(String msg, R data) {
        return new Builder<R>().code(ResponseEnum.SUCCESS.code()).data(data).msg(msg).build();
    }

    /**
     * 构建成功响应
     * @param data 数据
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> success(R data) {
        return new Builder<R>().code(ResponseEnum.SUCCESS.code()).data(data).msg(ResponseEnum.SUCCESS.text()).build();
    }

    /**
     * 构建成功响应（无数据）
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> success() {
        return new Builder<R>().code(ResponseEnum.SUCCESS.code()).msg(ResponseEnum.SUCCESS.text()).build();
    }

    /**
     * 构建失败响应
     * @param msg 消息
     * @param data 数据
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> error(String msg, R data) {
        return new Builder<R>().code(ResponseEnum.FAIL.code()).data(data).msg(msg).build();
    }

    /**
     * 构建失败响应
     * @param msg 消息
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> error(String msg) {
        return new Builder<R>().code(ResponseEnum.FAIL.code()).msg(msg).build();
    }

    /**
     * 构建失败响应
     * @param msg 消息
     * @param code 响应码
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> error(String msg, int code) {
        return new Builder<R>().code(code).msg(msg).build();
    }

    /**
     * 构建失败响应（无数据）
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> error() {
        return new Builder<R>().code(ResponseEnum.FAIL.code()).msg(ResponseEnum.FAIL.text()).build();
    }

    /**
     * 构建指定枚举的失败响应
     * @param responseEnum 响应枚举
     * @param <R> 数据类型
     * @return Response
     */
    public static <R> Response<R> error(ResponseEnum responseEnum) {
        return new Builder<R>().code(responseEnum.code()).msg(responseEnum.text()).build();
    }

    /**
     * Builder模式构建对象
     * @param <R> 数据类型
     */
    public static class Builder<R> {
        /**
         * 默认构造方法
         */
        public Builder() {}
        private String err = "";
        private String msg = "";
        private R data = null;
        private int code = ResponseEnum.FAIL.code();

        /**
         * 设置消息
         * @param msg 消息
         * @return Builder
         */
        public Builder<R> msg(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * 设置错误信息
         * @param err 错误信息
         * @return Builder
         */
        public Builder<R> err(String err) {
            this.err = err;
            return this;
        }

        /**
         * 设置数据
         * @param data 数据
         * @return Builder
         */
        public Builder<R> data(R data) {
            this.data = data;
            return this;
        }

        /**
         * 设置响应码
         * @param code 响应码
         * @return Builder
         */
        public Builder<R> code(int code) {
            this.code = code;
            return this;
        }

        /**
         * 构建Response对象
         * @return Response
         */
        public Response<R> build() {
            return new Response<>(this);
        }
    }
}
