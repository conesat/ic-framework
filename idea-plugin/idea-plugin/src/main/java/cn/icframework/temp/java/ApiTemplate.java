package cn.icframework.temp.java;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class ApiTemplate {

    public final static String API_TEMPLATE = """
            package #PACKAGE_NAME.api;
                        
            import cn.icframework.annotation.auth.RequireAuth;
            import cn.icframework.common.consts.Api;
            import api.basic.cn.icframework.core.BasicApi;
            import cn.icframework.core.common.bean.PageRequest;
            import cn.icframework.core.common.bean.PageResponse;
            import cn.icframework.core.common.bean.Response;
            import cn.icframework.mybatis.wrapper.SqlWrapper;
            import cn.icframework.system.consts.UserType;
            import #PACKAGE_NAME.#MODEL_NAME_FIST_UP;
            import #PACKAGE_NAME.pojo.dto.#MODEL_NAME_FIST_UPDTO;
            import #PACKAGE_NAME.pojo.vo.#MODEL_NAME_FIST_UPVO;
            import #PACKAGE_NAME.pojo.vo.#MODEL_NAME_FIST_UPVOConverter;
            import #PACKAGE_NAME.service.#MODEL_NAME_FIST_UPService;
            import #PACKAGE_NAME.wrapperbuilder.#MODEL_NAME_FIST_UPWrapperBuilder;
            import jakarta.annotation.Resource;
            import jakarta.servlet.http.HttpServletRequest;
            import org.springframework.validation.annotation.Validated;
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.PostMapping;
            import org.springframework.web.bind.annotation.RequestMapping;
            import org.springframework.web.bind.annotation.RequestParam;
            import org.springframework.web.bind.annotation.RestController;
            import org.springframework.web.bind.annotation.PathVariable;
            import org.springframework.web.bind.annotation.DeleteMapping;
            import org.springframework.web.bind.annotation.PutMapping;
            import lombok.RequiredArgsConstructor;
            
            import java.io.Serializable;
            import java.util.List;
                        
            /**
             * manage接口，用于管理后台
             *
             * @author #AUTHOR
             * @since #DATE
             */
            @RestController
            @RequestMapping(value = Api.API_SYS + "/#LU_CASE_TO_CHAR_SPLIT", name ="#TABLE_COMMENT")
            @RequireAuth(userType = UserType.SYSTEM_USER)
            @RequiredArgsConstructor
            public class ApiSys#MODEL_NAME_FIST_UP extends BasicApi {
                private final #MODEL_NAME_FIST_UPService #MODEL_NAME_FIST_DOWNService;
                private final #MODEL_NAME_FIST_UPVOConverter #MODEL_NAME_FIST_DOWNVOConverter;
                private final #MODEL_NAME_FIST_UPWrapperBuilder wrapperBuilder;
            
            
                /**
                 * 获取单个详情
                 *
                 * @param id [Serializable] *id
                 * @return
                 */
                @GetMapping(value = "/item/{id}", name = "获取详情")
                public Response<#MODEL_NAME_FIST_UPVO> detail(@PathVariable("id") Serializable id) {
                    return Response.success(#MODEL_NAME_FIST_DOWNService.selectById(id, #MODEL_NAME_FIST_UPVO.class));
                }
            
                /**
                 * 获取列表
                 *
                 * @param current  [int] 当前页码
                 * @param pageSize [int] 分页大小
                 * @return
                 */
                @PostMapping(value = "/page", name = "分页查询")
                public PageResponse<#MODEL_NAME_FIST_UPVO> page(HttpServletRequest request, PageRequest page) {
                    SqlWrapper sqlWrapper = wrapperBuilder.build(getQueryParams(request));
                    return #MODEL_NAME_FIST_DOWNService.page(sqlWrapper, page, #MODEL_NAME_FIST_UPVO.class);
                }
            
                /**
                 * 查询全部
                 *
                 * @return
                 */
                //@PostMapping(value = "/all", name = "查询全部")
                //public List<#MODEL_NAME_FIST_UPVO> all(HttpServletRequest request) {
                //    SqlWrapper sqlWrapper = wrapperBuilder.build(getQueryParams(request));
                //    return #MODEL_NAME_FIST_DOWNService.select(sqlWrapper, #MODEL_NAME_FIST_UPVO.class);
                //}
            
                /**
                 * 删除
                 *
                 * @param ids [Serializable[]] id列表
                 * @return
                 */
                @DeleteMapping(name = "删除")
                public Response<Void> delete(@RequestParam("ids") List<Serializable> ids) {
                    #MODEL_NAME_FIST_DOWNService.deleteByIds(ids);
                    return Response.success();
                }
                
                /**
                 * 编辑
                 */
                @PutMapping(name ="编辑")
                public Response<Void> edit(@Validated #MODEL_NAME_FIST_UPDTO dto) {
                    #MODEL_NAME_FIST_DOWNService.edit(dto);
                    return Response.success();
                }
                
                /**
                 * 新增
                 */
                @PostMapping(name ="新增")
                public Response<Void> create(@Validated #MODEL_NAME_FIST_UPDTO dto) {
                    #MODEL_NAME_FIST_DOWNService.edit(dto);
                    return Response.success();
                }
            }
            """;

    public final static String PUBLIC_TEMPLATE = """
            package #PACKAGE_NAME.api;
                        
            import cn.icframework.common.consts.Api;
            import api.basic.cn.icframework.core.BasicApi;
            import org.springframework.web.bind.annotation.RequestMapping;
            import org.springframework.web.bind.annotation.RestController;
                        
            /**
             * public
             * 用于公开内容
             *
             * @author #AUTHOR
             * @since #DATE
             */
            @RestController
            @RequestMapping(value = Api.API_PUBLIC + "/#LU_CASE_TO_CHAR_SPLIT", name ="#TABLE_COMMENT")
            public class ApiPublic#MODEL_NAME_FIST_UP extends BasicApi {
                        
            }
            """;
    public final static String APP_TEMPLATE = """
            package #PACKAGE_NAME.api;
                        
            import cn.icframework.annotation.auth.RequireAuth;
            import cn.icframework.common.consts.Api;
            import cn.icframework.system.consts.UserType;
            import api.basic.cn.icframework.core.BasicApi;
            import org.springframework.web.bind.annotation.RequestMapping;
            import org.springframework.web.bind.annotation.RestController;
                        
            /**
             * app 接口
             *
             * @author #AUTHOR
             * @since #DATE
             */
            @RequireAuth(userType = UserType.SYSTEM_USER)
            @RestController
            @RequestMapping(value = Api.API_APP + "/#LU_CASE_TO_CHAR_SPLIT", name ="#TABLE_COMMENT")
            public class ApiApp#MODEL_NAME_FIST_UP extends BasicApi {
                        
            }
            """;
}
