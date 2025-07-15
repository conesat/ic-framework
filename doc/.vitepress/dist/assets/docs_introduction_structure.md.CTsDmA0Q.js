import{_ as a,c as n,o as i,ae as e}from"./chunks/framework.Dgg8-8ov.js";const k=JSON.parse('{"title":"IC Framework 架构说明","description":"","frontmatter":{},"headers":[],"relativePath":"docs/introduction/structure.md","filePath":"docs/introduction/structure.md"}'),p={name:"docs/introduction/structure.md"};function l(r,s,t,o,c,g){return i(),n("div",null,s[0]||(s[0]=[e(`<h1 id="ic-framework-架构说明" tabindex="-1">IC Framework 架构说明 <a class="header-anchor" href="#ic-framework-架构说明" aria-label="Permalink to &quot;IC Framework 架构说明&quot;">​</a></h1><p>IC Framework 是一个面向中后台、App、小程序等多端的集成开发脚手架，采用模块化设计，便于扩展和维护。下文梳理各模块功能、依赖关系及整体架构。</p><h2 id="_1-模块总览" tabindex="-1">1. 模块总览 <a class="header-anchor" href="#_1-模块总览" aria-label="Permalink to &quot;1. 模块总览&quot;">​</a></h2><ul><li><strong>ic-framework-annotation</strong>：通用注解定义</li><li><strong>ic-framework-auth</strong>：权限认证体系</li><li><strong>ic-framework-cache</strong>：缓存接口与实现（本地/Redis/统一）</li><li><strong>ic-framework-common</strong>：通用工具、常量、配置</li><li><strong>ic-framework-core</strong>：核心基础设施、异常、配置</li><li><strong>ic-framework-dber</strong>：数据库增强与表结构管理</li><li><strong>ic-framework-gen</strong>：代码生成器（Java/Vue）</li><li><strong>ic-framework-mybatis</strong>：MyBatis 增强</li><li><strong>ic-framework-mybatis-processor</strong>：MyBatis 注解处理器</li><li><strong>ic-framework-spring-boot-starter</strong>：一键集成 starter</li><li><strong>ic-framework-gen-template</strong>：代码生成模板</li><li><strong>ic-framework-mybatis-processor-test</strong>：处理器测试</li><li><strong>idea-plugin</strong>：IDEA 插件</li></ul><h2 id="_2-依赖关系与调用流程" tabindex="-1">2. 依赖关系与调用流程 <a class="header-anchor" href="#_2-依赖关系与调用流程" aria-label="Permalink to &quot;2. 依赖关系与调用流程&quot;">​</a></h2><div class="language-mermaid vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">mermaid</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">graph TD</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  A[ic-framework-spring-boot-starter] --&gt; B[ic-framework-core]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  B --&gt; C[ic-framework-common]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  B --&gt; D[ic-framework-annotation]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  B --&gt; E[ic-framework-auth]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  B --&gt; F[ic-framework-cache]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  B --&gt; G[ic-framework-mybatis]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  G --&gt; H[ic-framework-mybatis-processor]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  B --&gt; I[ic-framework-dber]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  B --&gt; J[ic-framework-gen]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  J --&gt; K[ic-framework-gen-template]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  subgraph 其他</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    L[idea-plugin]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    M[ic-framework-mybatis-processor-test]</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  end</span></span></code></pre></div><h2 id="_3-各模块功能简介" tabindex="-1">3. 各模块功能简介 <a class="header-anchor" href="#_3-各模块功能简介" aria-label="Permalink to &quot;3. 各模块功能简介&quot;">​</a></h2><ul><li><strong>annotation</strong>：定义如 ApiCache、Author 等通用注解，供其他模块使用。</li><li><strong>auth</strong>：提供权限认证、用户体系、权限注解（如 RequireAuth、PermissionInit）。</li><li><strong>cache</strong>：统一缓存接口，支持本地、Redis 等多种实现。</li><li><strong>common</strong>：常用工具类、常量、枚举、全局配置。</li><li><strong>core</strong>：核心基础设施，异常处理、配置、工具方法。</li><li><strong>dber</strong>：数据库表结构管理、DDL 辅助。</li><li><strong>gen</strong>：代码生成器，支持 Java/Vue 等多端代码生成。</li><li><strong>mybatis</strong>：MyBatis 增强，注解、构建器、缓存、解析等。</li><li><strong>spring-boot-starter</strong>：一键集成所有核心模块。</li><li><strong>gen-template</strong>：代码生成模板。</li><li><strong>mybatis-processor</strong>：MyBatis 注解处理器。</li><li><strong>mybatis-processor-test</strong>：处理器测试。</li><li><strong>idea-plugin</strong>：IDEA 插件开发。</li></ul><h2 id="_4-架构特点" tabindex="-1">4. 架构特点 <a class="header-anchor" href="#_4-架构特点" aria-label="Permalink to &quot;4. 架构特点&quot;">​</a></h2><ul><li>模块化、可插拔</li><li>支持多端集成</li><li>内置权限、缓存、代码生成等常用能力</li><li>适合中后台、App、小程序等场景</li></ul><hr><blockquote><p>如需详细了解某个模块，请参考对应文档或源码。</p></blockquote><div class="language- vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang"></span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span>ic-framework-service</span></span>
<span class="line"><span>├─ic-framework-project</span></span>
<span class="line"><span>│  ├─src</span></span>
<span class="line"><span>│     ├─main</span></span>
<span class="line"><span>│        ├─java</span></span>
<span class="line"><span>│        └─resources</span></span>
<span class="line"><span>│            ├─i18n</span></span>
<span class="line"><span>│            ├─init</span></span>
<span class="line"><span>│            │  └─rp</span></span>
<span class="line"><span>│            └─sqls</span></span>
<span class="line"><span>├─ic-framework-system</span></span>
<span class="line"><span>   ├─src</span></span>
<span class="line"><span>      ├─main</span></span>
<span class="line"><span>         ├─java</span></span>
<span class="line"><span>         │  └─cn.icframework.system</span></span>
<span class="line"><span>         │    ├─config</span></span>
<span class="line"><span>         │    ├─consts</span></span>
<span class="line"><span>         │    ├─enums</span></span>
<span class="line"><span>         │    ├─module</span></span>
<span class="line"><span>         │    │  ├─setting</span></span>
<span class="line"><span>         │    │  │  ├─api</span></span>
<span class="line"><span>         │    │  │  ├─dao</span></span>
<span class="line"><span>         │    │  │  ├─pojo</span></span>
<span class="line"><span>         │    │  │  │  ├─dto</span></span>
<span class="line"><span>         │    │  │  │  └─vo</span></span>
<span class="line"><span>         │    │  │  ├─service</span></span>
<span class="line"><span>         │    │  │  └─wrapperbuilder</span></span>
<span class="line"><span>         │    │  ├─...</span></span>
<span class="line"><span>         │    ├─runner</span></span>
<span class="line"><span>         │    └─utils</span></span>
<span class="line"><span>         └─resources</span></span>
<span class="line"><span>             ├─init</span></span>
<span class="line"><span>             │  └─rp</span></span>
<span class="line"><span>             └─sqls</span></span></code></pre></div><h3 id="resources-init-rp" tabindex="-1">resources/init/rp <a class="header-anchor" href="#resources-init-rp" aria-label="Permalink to &quot;resources/init/rp&quot;">​</a></h3><p>该目录存放需要初始化的角色权限关系，下面存放两个文件role.json是初始化角色列表，rolePermissions.json是初始化角色权限关系。</p><div class="language- vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang"></span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span>init.rp</span></span>
<span class="line"><span>├─rolePermissions.json</span></span>
<span class="line"><span>└─role.json</span></span></code></pre></div>`,16)]))}const d=a(p,[["render",l]]);export{k as __pageData,d as default};
