package cn.icframework.temp.java;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class OTemplate {

    public final static String DTO_TEMPLATE = """
            package #PACKAGE_NAME.pojo.dto;
                        
            #IMPORTS
            import lombok.Getter;
            import lombok.Setter;
                        
            /**
             * @author #AUTHOR
             * @since #DATE
             */
            @Getter
            @Setter
            public class #MODEL_NAME_FIST_UPDTO {
            #CONTENT
            }
            """;

    public final static String VO_TEMPLATE = """
            package #PACKAGE_NAME.pojo.vo;
                        
            #IMPORTS
            import lombok.Getter;
            import lombok.Setter;
                        
            /**
             * @author #AUTHOR
             * @since #DATE
             */
            @Getter
            @Setter
            public class #MODEL_NAME_FIST_UPVO {
            #CONTENT
            }
            """;

    public final static String DTO_CONVERT_TEMPLATE = """
            package #PACKAGE_NAME.pojo.vo;
                        
            import pojo.basic.cn.icframework.core.BasicConverter;
            import #PACKAGE_NAME.#MODEL_NAME_FIST_UP;
            import org.springframework.stereotype.Component;
                        
            /**
             * @author #AUTHOR
             * @since #DATE
             */
            @Component
            public class #MODEL_NAME_FIST_UPVOConverter extends BasicConverter<#MODEL_NAME_FIST_UP, #MODEL_NAME_FIST_UPVO> {
            
            }
            """;
}
