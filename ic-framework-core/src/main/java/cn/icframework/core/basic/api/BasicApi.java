package cn.icframework.core.basic.api;

import cn.icframework.core.basic.wrapperbuilder.QueryParams;
import cn.icframework.core.common.consts.ParamsConst;
import cn.icframework.core.utils.Assert;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基础API类
 *
 * 提供基础API相关功能。
 */
public class BasicApi {
    /**
     * 默认构造方法
     */
    public BasicApi() {}

    /**
     * 获取查询参数，包括基础查询字段和自定义字段。
     *
     * @param request Http请求对象
     * @return 查询参数对象
     */
    protected QueryParams getQueryParams(HttpServletRequest request) {
        return getQueryParams(request, (String[]) null);
    }

    /**
     * 获取查询参数，包括基础查询字段和自定义字段。
     *
     * @param request Http请求对象
     * @param keys 额外需要获取的参数名
     * @return 查询参数对象
     */
    protected QueryParams getQueryParams(HttpServletRequest request, String... keys) {
        QueryParams params = new QueryParams();
        Set<String> paramKeys = Stream.of(ParamsConst.SEARCH_KEY, ParamsConst.ORDERS).collect(Collectors.toSet());
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (keys != null) {
            paramKeys.addAll(Arrays.asList(keys));
        } else {
            paramKeys = parameterMap.keySet();
        }
        for (String key : paramKeys) {
            String[] values = parameterMap.get(key);
            String join = String.join("", values);
            if (!StringUtils.hasLength(join)) {
                continue;
            }
            params.put(key, values);
        }
        return params;
    }

    /**
     * 检查请求是否包含指定参数，不存在则抛出异常。
     *
     * @param request Http请求对象
     * @param key 参数名
     */
    protected void checkParamExist(HttpServletRequest request, String... key) {
        for (String s : key) {
            Assert.isNotEmpty(request.getParameter(s), s + ":不能为空");
        }
    }

}
