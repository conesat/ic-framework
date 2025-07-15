package cn.icframework.temp.java;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class DefTemplate {
    public final static String DEF_TEMPLATE = """
            package #PACKAGE_NAME.def;

            import #PACKAGE_NAME.#MODEL_NAME_FIST_UP;
            import cn.icframework.mybatis.query.QueryTable;
            import cn.icframework.mybatis.query.QueryField;
                        
            /**
             * 请勿修改该文件，该文件由ic mybatis生成会被覆盖
             * @author #AUTHOR
             * @since #DATE
             */
            public class #MODEL_NAME_FIST_UPDef extends QueryTable<#MODEL_NAME_FIST_UPDef> {
                
                public #MODEL_NAME_FIST_UPDef(Class<?> defClass) {
                    super(defClass);
                }
                
                public static #MODEL_NAME_FIST_UPDef table() {
                    return new #MODEL_NAME_FIST_UPDef(#MODEL_NAME_FIST_UP.class);
                }
                
                @Override
                public #MODEL_NAME_FIST_UPDef newInstance() {
                    return new #MODEL_NAME_FIST_UPDef(#MODEL_NAME_FIST_UPDef.class);
                }
                
                public QueryField<#MODEL_NAME_FIST_UPDef> _all= new QueryField<>(this, "*");
                
            #CONTENT
            }
            """;
}
