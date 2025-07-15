package cn.icframework.gen.template.java;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class ServiceTemplate {

    public final static String SERVICE_TEMPLATE = """
            package #PACKAGE_NAME.service;
                        
            import service.basic.cn.icframework.core.BasicService;
            import utils.cn.icframework.core.BeanUtils;
            import #PACKAGE_NAME.#MODEL_NAME_FIST_UP;
            import #PACKAGE_NAME.pojo.dto.#MODEL_NAME_FIST_UPDTO;
            import #PACKAGE_NAME.dao.#MODEL_NAME_FIST_UPMapper;
            import #PACKAGE_NAME.wrapperbuilder.#MODEL_NAME_FIST_UPWrapperBuilder;
            import org.springframework.stereotype.Service;
            import org.springframework.transaction.annotation.Transactional;
            import org.springframework.util.StringUtils;
                       
            /**
             * @author #AUTHOR
             * @since #DATE
             */
            @Service
            public class #MODEL_NAME_FIST_UPService extends BasicService<#MODEL_NAME_FIST_UPMapper, #MODEL_NAME_FIST_UP> {
                       
                /**
                 * 编辑或者保存
                 * @param dto
                 */
                @Transactional
                public void edit(#MODEL_NAME_FIST_UPDTO dto) {
                    #MODEL_NAME_FIST_UP entity = dto.getId() != null ? selectById(dto.getId()) : #MODEL_NAME_FIST_UP.def();
                    BeanUtils.copyExcludeProps(dto, entity);
                    if (#ID_EMPTY) {
                        updateById(entity);
                    } else {
                        insert(entity);
                    }
                }
            }
            """;
}
