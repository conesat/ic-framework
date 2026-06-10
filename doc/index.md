---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "IC Framework"
  text: "集成开发脚手架"
  tagline: 面向中后台与多端业务的集成开发框架，先把系统能力接好，再把精力放到业务上
  actions:
    - theme: brand
      text: 🚀 先跑起来
      link: /docs/start/import
    - theme: alt
      text: 📦 看整体结构
      link: /docs/introduction/structure
    - theme: alt
      text: 🔗 后台预览 test/Aa123456
      link: http://hotel.chinahg.top
  image:
    src: /imgs/logo.png

features:
  - icon: 🧩
    title: Spring Boot 3.5.3 +
    details: 最低支持 Spring Boot 3.5.3
  - icon: ☕️
    title: Java 25+
    details: 最低支持 JDK 25
  - icon: 📱
    title: 多端集成
    details: 支持中台、小程序、App
  - icon: ⚡️
    title: 代码生成
    details: 内置代码生成器，提升开发效率
  - icon: 🔒
    title: 权限与缓存
    details: 内置权限认证与多种缓存实现
  - icon: 🛢️
    title: IcMybatis
    details: mybatis增强，减少sql调整
  - icon: 🛠
    title: Dber
    details: 自动实体ddl，sql版本升级
  - icon: 🔑
    title: 集成鉴权
    details: 角色权限集成

---

<div style="margin: 2em 0; text-align: center; font-size: 1.1em;">
  <b>⚠️ 框架处于开发阶段，欢迎有兴趣的开发者参与完善！</b>
</div>

## 📚 文档导航

- [先跑通示例工程](/docs/start/import)
- [理解项目结构](/docs/introduction/structure)
- [按规范开发一个模块](/docs/start/java)
- [理解 system 模块](/docs/system/system)
- [学习 ic-mybatis 查询](/docs/ic-mybatis/query)

## 🌐 社区与贡献

- GitHub: [ic-framework](https://github.com/conesat/ic-framework)
- Gitee: [ic-framework](https://gitee.com/ic-framework)
- Issue/PR 欢迎提交
- QQ邮箱：1092501244@qq.com

---

<div class="ic-app-ad">
  <div class="ic-app-text">
    <h3>IcFramework开发的app</h3>
    <div style="margin-top:30px">下载体验短线客App，AI智能炒股训练场！</div>
    <br>
    <a href="http://dxk.chinahg.top/" target="_blank" class="ic-app-btn">
        <button style="color: #fff">
            立即下载
        </button>
    </a>
  </div>
  <img src="/imgs/dxk.png" alt="短线客App二维码" class="ic-app-qrcode">
</div>

<div class="ic-footer">
  <span>让开发者专注业务，基础设施交给框架！</span>
  <br/>
  <a href="https://beian.miit.gov.cn/" target="_blank">桂ICP备17009456号-4</a>
</div>
