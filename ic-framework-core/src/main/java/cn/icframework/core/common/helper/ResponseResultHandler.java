package cn.icframework.core.common.helper;


import cn.icframework.core.common.bean.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;



/**
 * 统一响应结果处理器。
 * <p>
 * 使用 {@code @ControllerAdvice} 和 {@code ResponseBodyAdvice} 拦截 Controller 方法默认返回参数，统一处理返回值/响应体。
 * </p>
 * @author hzl
 * @since 2023/5/7 0007
 */
@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {
    /**
     * 默认构造方法
     */
    public ResponseResultHandler() {}

    /**
     * 判断是否需要执行 beforeBodyWrite 方法。
     * @param arg0 方法参数
     * @param arg1 消息转换器类型
     * @return true 执行，false 不执行
     */
    @Override
    public boolean supports(@NotNull MethodParameter arg0, @NotNull Class<? extends HttpMessageConverter<?>> arg1) {
        return true;
    }

    /**
     * 对返回值做包装处理。
     * @param body 返回体
     * @param arg1 方法参数
     * @param arg2 媒体类型
     * @param arg3 消息转换器类型
     * @param arg4 请求
     * @param arg5 响应
     * @return 包装后的响应体
     */
    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter arg1, @NotNull MediaType arg2,
                                  @NotNull Class<? extends HttpMessageConverter<?>> arg3, @NotNull ServerHttpRequest arg4, @NotNull ServerHttpResponse arg5) {
        if (body instanceof String) {
            ObjectMapper om = new ObjectMapper();
            try {
                return om.writeValueAsString(Response.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (body instanceof Response) {
            return body;
        }
        return Response.success(body);
    }
}