# 导入项目

## 拉取代码

默认是main分支。如果你想查看hotel代码，请切换hotel分支

作者的环境（jdk是必须的21+）：

- JDK 21+ (必须)
- MySQL 5.7
- Maven 3.9.10 (必须设置maven的jdk为21+)
- Node v23.11.0
- Npm 8.13.2
- Pnpm 10.9.0 (可以只用npm或者cnpm看个人习惯)

## clone或下载github项目

地址：[ic-framework-service](https://github.com/conesat/ic-framework-service)
![](/public/imgs/service.png)

>

## 设置source

target/generated-sources/annotations下会生成IcMybatis生成的代码，需要设置为source
（这块逻辑类似mybatis-flex：ps这是个很nice的框架ic-mybatis有许多地方都借鉴它）
![](/public/imgs/project-setting.png)

>

## 配置数据库与oss

**记得先创建好数据库哦**作者字符集选用：utf-8 规则： utf8_general_ci

oss用于存储上传文件如用户头像、公告图片，没有可以先留空，当然也可以改为本地存储或者fastdfs【但是作者还没完善，欢迎提交pr】
![](/public/imgs/dev.png)

## 启动项目

运行 project 项目下的 cn.icframework.project.ProjectApplication

正常情况下，会看到以下输出则java启动成功。

```log
......
2025-07-18T14:03:25.204+08:00  INFO 24416 --- [ic-project] [           main] cn.icframework.dber.DDLHelper            : CREATE TABLE IF NOT EXISTS `sys_setting` (
 `id` bigint NOT NULL ,
 `name` varchar(30) NOT NULL  COMMENT '系统名称',
 `ad_type` int NULL  COMMENT '首页广告类型',
 `app_ad_type` int NULL  COMMENT 'App开屏广告类型',
 `ad_file_url` varchar(255) NULL  COMMENT '首页广告文件地址',
 `app_ad_file_url` varchar(255) NULL  COMMENT 'App开屏广告文件地址',
 `out_date_time` datetime NULL  COMMENT '过期时间',
 `ad_url` varchar(255) NULL  COMMENT '首页广告跳转地址',
 `activate_time` datetime NULL  COMMENT '激活时间',
 `app_ad_url` varchar(255) NULL  COMMENT 'App开屏广告跳转地址',
 `domain` varchar(255) NOT NULL  COMMENT '域名',
 `activation_code` varchar(1000) NOT NULL  COMMENT '激活码',
 `logo_file_id` varchar(255) NULL  COMMENT 'logo'
, PRIMARY KEY ( `id` )


)  ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统设置';

2025-07-18T14:03:25.374+08:00  INFO 24416 --- [ic-project] [           main] c.i.cache.impl.UnifiedCacheServiceImpl   : Cache service initialized - Redis available: false
2025-07-18T14:03:26.366+08:00  INFO 24416 --- [ic-project] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 9999 (http) with context path '/api'
2025-07-18T14:03:26.379+08:00  INFO 24416 --- [ic-project] [           main] c.i.project.ProjectApplication           : Started ProjectApplication in 7.372 seconds (process running for 8.474)
 
  _____ _____   ______                                           _    
 |_   _/ ____| |  ____|                                         | |   
   | || |      | |__ _ __ __ _ _ __ ___   _____      _____  _ __| | __
   | || |      |  __| '__/ _` | '_ ` _ \ / _ \ \ /\ / / _ \| '__| |/ /
  _| || |____  | |  | | | (_| | | | | | |  __/\ V  V / (_) | |  |   < 
 |_____\_____| |_|  |_|  \__,_|_| |_| |_|\___| \_/\_/ \___/|_|  |_|\_\
 =============================================================================                        
 << IC Framework V1.0.0 -- start successful >>                                                               

2025-07-18T14:03:26.591+08:00  INFO 24416 --- [ic-project] [           main] cn.icframework.dber.DDLHelper            : ALTER TABLE sys_user_role ADD CONSTRAINT `FK_sys_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
......
```

## 前端配置

完善dev后台地址配置，在_web/admin完善以下文件

- .env
- .env.development
- .env.site
- .env.test

>
内容如下：

```
# 打包路径
VITE_BASE_URL = /
VITE_IS_REQUEST_PROXY = true
# 后台地址接口
VITE_API_URL = http://localhost:9999
# 接口前缀
VITE_API_URL_PREFIX = /api
```

![](/public/imgs/web-dev.png)

## 安装依赖

终端进入/_web/admin 执行下面安装命令

```cmd
npm i
```

作者用 pnpm

```cmd
pnpm i
```

## 启动

如果一切顺利接下来就可以启动前端项目了

```cmd
npm run dev
```

作者用 pnpm

```cmd
pnpm run dev
```

顺利的话会看到以下输出

```log
......
VITE v5.4.19  ready in 3942 ms

➜  Local:   http://localhost:3002/
➜  Network: http://192.168.23.110:3002/
➜  Network: http://192.168.56.1:3002/
➜  Network: http://192.168.137.1:3002/
➜  Network: http://172.17.96.1:3002/
➜  Network: http://172.19.64.1:3002/
➜  Network: http://172.30.240.1:3002/
➜  Network: http://172.25.144.1:3002/
➜  Network: http://172.21.112.1:3002/
➜  press h + enter to show help
```

## 激活系统
![](/public/imgs/init.png)
>
初始化需要秘钥：生成秘钥请在project项目system/test/CodeGen.java
>
RLK2ihRbHkQlE99U3DIoQfEwlb8hR1Dz1K0icaFzIT0DXHJcZ7lw73VhyRNYmdZamcNjEx-lkTT4uc2DHxc6kvrB_Akb0DCmAk57xyCIhMbD75J94GpFp191I7HZ4U4YddHCLSpHDvYGmyGhGZnnMgQCmV1S8zY7QpV29GPA8YkLrAeW_5KlT5xPuJDNUibh_0Il>
BSE7b5GWBNSyUg6xXbx-h0Bta-gicHatNV7toED7P0IVGlCyW3OVTEJY_Q-1cYBMb6v1WJn2D8B5pGcuHeMmmEuGSenzbMCWERU2SwStPVLiqJNRDRD9WDVPBH7LDx0hVQ7OntxvoMYCj5xxtQ 