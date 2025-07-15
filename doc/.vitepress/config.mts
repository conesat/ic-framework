import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
    title: "IC Framework",
    description: "IC Framework",
    themeConfig: {
        // https://vitepress.dev/reference/default-theme-config
        nav: [
            {text: '首页', link: '/'},
            {text: '文档', link: '/docs/introduction/about-ic'}
        ],

        sidebar: [
            {
                text: '简介',
                items: [
                    {text: '关于IC', link: '/docs/introduction/about-ic'},
                    {text: '项目结构', link: '/docs/introduction/structure'},
                    {text: '集成框架', link: '/docs/introduction/project'},
                ]
            },
            {
                text: '文档',
                items: [
                    {text: 'IC配置', link: '/docs/base/ic-config'},
                    {text: '角色权限', link: '/docs/base/rp'},
                    {text: '表实体', link: '/docs/base/entity'},
                    {text: '表映射', link: '/docs/base/def'},
                    {text: 'API接口', link: '/docs/base/api'},
                    {text: 'WrapperBuilder', link: '/docs/base/wrapperbuilder'},
                    {text: 'Service', link: '/docs/base/service'},
                    {text: 'Mapper', link: '/docs/base/mapper'},
                    {text: 'Pojo', link: '/docs/base/pojo'},
                ]
            },
            {
                text: 'IC-Mybatis详细使用',
                items: [
                    {text: '使用', link: '/docs/ic-mybatis/ic-mybatis'},
                    {text: '查询', link: '/docs/ic-mybatis/query'},
                    {text: '注解', link: '/docs/ic-mybatis/annotations'},
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/vuejs/vitepress'}
        ]
    }
})
