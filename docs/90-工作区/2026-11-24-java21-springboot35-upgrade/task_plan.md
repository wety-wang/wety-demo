# 任务计划：后端升级至 JDK 21 与 Spring Boot 3.5.16

## 1. 目标与范围

将 `wety-demo-api` 后端项目从 JDK 8 + Spring Boot 2.3.11.RELEASE 升级至 JDK 21 + Spring Boot 3.5.16，同时完成 Jakarta EE 迁移、Swagger 2 / Knife4j 2 → OpenAPI 3 / Knife4j 4 迁移、Hibernate 6.6 / Spring Data JPA 3.5 适配，并在本机 JDK 21 + Docker MySQL 8（端口 3307）环境下完成编译打包与实机启动验证。

## 2. 职责分工与实施步骤

| 阶段 | 负责角色 | 主要内容 | 状态 |
| --- | --- | --- | --- |
| 1. 构建与配置升级 | `build-config` | 升级 `pom.xml` 依赖与插件（含补充 `org.openjdk.nashorn:nashorn-core:15.6` 兼容 JDK 21 SM2 运行期）、调整 `application*.yaml` 配置、检查 `log4j.xml` 兼容性、验证 `mvn dependency:resolve`、维护工作区过程文档 | 已完成 |
| 2. 核心层与通用层迁移 | `core-common` | `Application.java`、`core/**`、`common/**` 的 `javax.*` → `jakarta.*` 迁移、OpenAPI 3 / Knife4j 4 配置与放行路径迁移、Hibernate 6 / Spring 6 适配、修复 `SM2Utils` 受检异常 | 已完成 |
| 3. 业务模块与测试迁移 | `module-migrator` | `module/**`、`src/test/**` 的 `javax.*` → `jakarta.*` 迁移、Swagger 2 → OpenAPI 3 注解迁移、JUnit 5 / Spring Data JPA 3.5 兼容性检查、补充 `SM2UtilsTest` 单测 | 进行中 |
| 4. 编译与实机联调验证 | `verifier` | 确认 Docker `mysql8`（3307）`wety_demo` 库就绪；执行 JDK 21 `mvn clean package`；实机启动 jar 验证 6676 端口、`/bac/common/getVerifyImage`、`/v3/api-docs`、`/doc.html`；清理临时进程并检查最终规范 | 已完成（待复验 SM2 单测） |

## 3. 验收标准

- [x] `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home mvn -f wety-demo-api/pom.xml dependency:resolve` 成功。
- [x] `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home mvn -f wety-demo-api/pom.xml clean package` 编译与测试全部通过（首轮 26/26 通过，待补充 `SM2UtilsTest` 后复验）。
- [x] 源码中 `javax.servlet` / `javax.persistence` / `javax.annotation` / `javax.validation` 及 `io.swagger.annotations` / `springfox` 均为 0 命中。
- [x] 使用 JDK 21 实机启动 `wety-demo-api`（`dev` profile，连接 3307 端口 MySQL 8）无启动异常，`/bac/common/getVerifyImage`、`/v3/api-docs`、`/doc.html` 均返回 200。
- [x] 过程文档三件套（`task_plan.md`、`findings.md`、`progress.md`）位于 `docs/90-工作区/2026-11-24-java21-springboot35-upgrade/`，仓库根目录无同名文件。
