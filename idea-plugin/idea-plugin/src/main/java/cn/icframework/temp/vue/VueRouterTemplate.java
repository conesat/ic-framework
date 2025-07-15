package cn.icframework.temp.vue;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class VueRouterTemplate {
    public final static String VUE_ROUTER_TEMPLATE = """
            import Layout from '@/layouts/index.vue';
                        
            export default [
              {
                path: '#MODULE',
                name: '#MODULE',
                component: Layout,
                redirect: '#MODULE/#MODEL_SPLIT_NAME-index',
                meta: {title: '#TABLE_NAME', icon: 'layers'},
                children: [
                  {
                    path: '#MODEL_SPLIT_NAME-index',
                    name: '#MODEL_SPLIT_NAME-index',
                    component: () => import('@/pages#MODULE/#MODEL_SPLIT_NAME/index.vue'),
                    meta: {title: '#TABLE_NAME'},
                  },
                  {
                    path: '#MODEL_SPLIT_NAME-edit',
                    name: '#MODEL_SPLIT_NAME-edit',
                    component: () => import('@/pages#MODULE/#MODEL_SPLIT_NAME/edit.vue'),
                    meta: {title: '编辑#TABLE_NAME', hidden: true, parent: '#MODULE/#MODEL_SPLIT_NAME-index'},
                  },
                ],
              }
            ];
            """;
}
