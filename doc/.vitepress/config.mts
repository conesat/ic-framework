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
                    {text: '角色权限初始化', link: '/docs/base/rp'},
                    {text: '岗位初始化', link: '/docs/base/pos'},
                    {text: '表实体', link: '/docs/base/entity'},
                    {text: 'API接口', link: '/docs/base/api'},
                    {text: 'WrapperBuilder', link: '/docs/base/wrapperbuilder'},
                    {text: 'Service', link: '/docs/base/service'},
                    {text: 'Mapper', link: '/docs/base/mapper'},
                    {text: 'Pojo', link: '/docs/base/pojo'},
                ]
            },
            {
                text: 'IC-Mybatis',
                items: [
                    {text: '开始', link: '/docs/ic-mybatis/ic-mybatis'},
                    {text: '基础查询', link: '/docs/ic-mybatis/query'},
                    {text: '复杂查询', link: '/docs/ic-mybatis/query-dif'},
                    {text: '结果映射', link: '/docs/ic-mybatis/query-as'},
                    {text: '插入', link: '/docs/ic-mybatis/insert'},
                    {text: '更新', link: '/docs/ic-mybatis/update'},
                ]
            },
            {
                text: 'Dber',
                items: [
                    {text: '配置', link: '/docs/dber/config'},
                    {text: '使用', link: '/docs/dber/dber'},
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/conesat/ic-framework'}
        ]
    }
})
