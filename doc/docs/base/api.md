# Api

以下是代码生成器生成的结构。
其中使用的getQueryMap方法需要继承 BasicApi 类，如果不需要使用这个快捷获取参数的方式，也可以不继承。

Api.API_MANAGE 是系统推荐的接口前缀，表明该接口为管理员接口，并且会校验系统是否有效。

此外还有
- API_PUBLIC不鉴权的接口，内部无法获取token。
- API_PROTECT需要鉴权，不校验系统有效性。


@RequireAuth 表名该接口需要鉴权

```java
package cn.icframework.system.module.dep.api;

import cn.icframework.common.annotation.auth.RequireAuth;
import consts.cn.icframework.common.Api;
import consts.cn.icframework.common.RequestValue;
import api.basic.cn.icframework.core.BasicApi;
import bean.common.cn.icframework.core.PageRequest;
import bean.common.cn.icframework.core.PageResponse;
import bean.common.cn.icframework.core.Response;
import wrapper.cn.icframework.mybatis.SqlWrapper;
import cn.icframework.system.consts.UserType;
import cn.icframework.system.module.dep.pojo.dto.DeptDTO;
import cn.icframework.system.module.dep.pojo.vo.DeptVO;
import cn.icframework.system.module.dep.pojo.vo.DeptVOConverter;
import cn.icframework.system.module.dep.service.DeptService;
import cn.icframework.system.module.dep.wrapperbuilder.DeptWrapperBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author create by ic gen
 * @since 2023/06/21
 */
@RestController
@RequestMapping(value = Api.API_MANAGE + "/dept", name = "部门（管理员）")
@RequireAuth(userType = UserType.MANAGER) // 必须是管理员才能访问
@RequiredArgsConstructor
public class ApiManageDept extends BasicApi {
    private final DeptService deptService;
    private final DeptVOConverter deptVOConverter;
    private final DeptWrapperBuilder deptWrapperBuilder;

    /**
     * 获取单个详情
     *
     * @param id [Serializable] *id
     * @return Response<DeptVO>
     */
    @GetMapping(value = "/{id}", name = "获取详情")
    public Response<DeptVO> detail(@PathVariable("id") Serializable id) {
        Map<String, RequestValue> params = new HashMap<>();
        params.put("id", new RequestValue(new String[]{id.toString()}));
        SqlWrapper sqlWrapper = deptWrapperBuilder.build(params);
        return Response.success(deptService.selectOneAs(sqlWrapper, DeptVO.class));
    }

    /**
     * 删除
     * @param ids [Serializable[]] id列表
     * @return Response
     */
    @DeleteMapping(name = "删除")
    public Response<Void> delete(@RequestParam("ids") List<Serializable> ids) {
        deptService.deleteByIds(ids);
        return Response.success();
    }

    /**
     * 编辑或者保存
     * @return Response
     */
    @PutMapping(name = "编辑")
    public Response<Void> edit(@Validated DeptDTO form) {
        deptService.edit(form);
        return Response.success();
    }

    /**
     * 新增
     */
    @PostMapping(name = "新增")
    public Response<Void> create(@Validated DeptDTO form) {
        deptService.edit(form);
        return Response.success();
    }

    /**
     * 获取列表
     *
     * @param current  [int] 当前页码
     * @param pageSize [int] 分页大小
     * @return PageResponse<DeptVO>
     */
    @PostMapping(value = "/page", name = "分页查询")
    public PageResponse<DeptVO> page(HttpServletRequest request, PageRequest page) {
        SqlWrapper sqlWrapper = deptWrapperBuilder.build(getQueryMap(request));
        return deptService.pageAs(page, sqlWrapper, DeptVO.class);
    }

    /**
     * 查询全部
     * @return List<DeptVO>
     */
    @PostMapping(value = "/all", name = "查询全部")
    public Response<List<DeptVO>> all(HttpServletRequest request) {
        SqlWrapper sqlWrapper = deptWrapperBuilder.build(getQueryMap(request));
        return Response.success(deptService.selectAs(sqlWrapper, DeptVO.class));
    }
}
```
