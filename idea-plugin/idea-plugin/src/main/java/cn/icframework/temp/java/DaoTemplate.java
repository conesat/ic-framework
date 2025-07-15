package cn.icframework.temp.java;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class DaoTemplate {

    public final static String DAO_TEMPLATE = """
            package #PACKAGE_NAME.dao;
                        
            import mapper.cn.icframework.mybatis.BasicMapper;
            import #PACKAGE_NAME.#MODEL_NAME_FIST_UP;
            import org.apache.ibatis.annotations.Mapper;
                        
            /**
            * @author #AUTHOR
            * @since #DATE
            */
            @Mapper
            public interface #MODEL_NAME_FIST_UPMapper extends BasicMapper<#MODEL_NAME_FIST_UP> {
            }
            """;
}
