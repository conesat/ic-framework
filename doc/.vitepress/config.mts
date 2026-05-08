import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
    title: "IC Framework",
    description: "IC Framework", head: [
        ['link', { rel: 'icon', href: '/favicon.ico' }]
    ],
    themeConfig: {
        // https://vitepress.dev/reference/default-theme-config
        nav: [
            { text: '首页', link: '/' },
            { text: '更新日志', link: '/docs/log/log' },
            {
                text: '获取代码',
                items: [
                    { text: 'GitHub', link: 'https://github.com/conesat/ic-framework' },
                    { text: 'Gitee', link: 'https://gitee.com/ic-framework/ic-framework' },
                    { text: '集成项目【GitHub】', link: 'https://github.com/conesat/ic-framework-service' },
                    { text: '集成项目【Gitee】', link: 'https://gitee.com/ic-framework/ic-framework-service' },
                    { text: '集成后台预览', link: 'http://hotel.chinahg.top' }
                ]
            }
        ],

        sidebar: [
            {
                text: '开始这里',
                items: [
                    { text: '关于IC', link: '/docs/introduction/about-ic' },
                    { text: '项目结构', link: '/docs/introduction/structure' },
                    { text: '导入与启动', link: '/docs/start/import' },
                ]
            },
            {
                text: '开发流程',
                items: [
                    { text: '后端开发结构', link: '/docs/start/java' },
                    { text: '前端开发结构', link: '/docs/start/vue' },
                    { text: '代码生成', link: '/docs/start/code_gen' },
                ]
            },
            {
                text: '核心开发',
                items: [
                    { text: 'IC配置', link: '/docs/base/ic-config' },
                    { text: '表实体', link: '/docs/base/entity' },
                    { text: 'API接口', link: '/docs/base/api' },
                    { text: 'WrapperBuilder', link: '/docs/base/wrapperbuilder' },
                    { text: 'Service', link: '/docs/base/service' },
                    { text: 'Mapper', link: '/docs/base/mapper' },
                    { text: 'Pojo', link: '/docs/base/pojo' },
                ]
            },
            {
                text: '系统模块',
                items: [
                    { text: 'ic-framework-system', link: '/docs/system/system' },
                    { text: '系统初始化', link: '/docs/system/init' },
                ]
            },
            {
                text: 'IC-Mybatis',
                items: [
                    { text: '开始', link: '/docs/ic-mybatis/ic-mybatis' },
                    { text: '基础查询', link: '/docs/ic-mybatis/query' },
                    { text: '复杂查询', link: '/docs/ic-mybatis/query-dif' },
                    { text: '结果映射', link: '/docs/ic-mybatis/query-as' },
                    { text: '插入', link: '/docs/ic-mybatis/insert' },
                    { text: '更新', link: '/docs/ic-mybatis/update' },
                    { text: '删除', link: '/docs/ic-mybatis/delete' },
                ]
            },
            {
                text: '扩展模块',
                items: [
                    { text: '权限 (Auth)', link: '/docs/auth/auth' },
                    { text: '缓存 (Cache)', link: '/docs/cache/cache' },
                    { text: 'Dber 配置', link: '/docs/dber/config' },
                    { text: 'Dber 使用', link: '/docs/dber/dber' },
                    { text: '代码生成器模块', link: '/docs/gen/gen' },
                    { text: '基础核心 (Core)', link: '/docs/core/core' },
                    { text: '通用工具 (Common)', link: '/docs/common/utils' },
                ]
            },
            {
                text: '其他',
                items: [
                    { text: '更新日志', link: '/docs/log/log' },
                    { text: 'Java25 升级说明', link: '/docs/start/java25_upgrade' },
                ]
            }
        ],
    }
})
