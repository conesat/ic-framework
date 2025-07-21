package cn.icframework.temp.vue;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class VueRouterJsonTemplate {
    public final static String VUE_ROUTER_JSON_TEMPLATE = """
              {
                "path": "#MODULE",
                "name": "#TABLE_NAME",
                "menuType": "Layout",
                "redirect": "#MODULE/#MODEL_SPLIT_NAME-index",
                "iconType": 2,
                "userType": "USER",
                "icon": "layers",
                "children": [
                  {
                    "path": "#MODEL_SPLIT_NAME-index",
                    "url": "#MODULE/#MODEL_SPLIT_NAME/index.vue",
                    "name": "#TABLE_NAME列表"
                  },
                  {
                    "path": "#MODEL_SPLIT_NAME-edit",
                    "url": "#MODULE/#MODEL_SPLIT_NAME/edit.vue",
                    "belong": "#MODULE/#MODEL_SPLIT_NAME-index",
                    "hidden": true,
                    "name": "编辑#TABLE_NAME"
                  }
                ]
              }
            """;
}
