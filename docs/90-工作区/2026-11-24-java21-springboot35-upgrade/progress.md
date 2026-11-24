# 进度记录：后端升级至 JDK 21 与 Spring Boot 3.5.16

## 2026-11-24

### build-config（构建、配置与过程文档）
- [x] 检查源码 `fastjson` 引用（0 命中）与 `javax.validation` / `jakarta.validation` 引用（0 命中）。
- [x] 更新 `wety-demo-api/pom.xml` 至 JDK 21 + Spring Boot 3.5.16 及全部目标依赖、插件托管配置，并在 `web` / `data-jpa` / `test` starter 中排除 `spring-boot-starter-logging` 防止与 Log4j2 冲突。
- [x] 更新 `application.yaml`、`application-dev.yaml`、`application-test.yaml`，确认 `log4j.xml` 兼容 Log4j 2.24.3 无需调整。
- [x] 执行 `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home mvn -f wety-demo-api/pom.xml -U dependency:resolve`，首次下载期间遇到一次偶发 TLS 握手中断，重试后全部解析成功（`BUILD SUCCESS`），`dependency:tree` 确认无 Logback 残留。
- [x] 创建工作区过程文档 `task_plan.md`、`findings.md`、`progress.md` 并更新 `docs/90-工作区/README.md`。

### 其他成员进度与联调验证记录（来自协作板同步）
- `verifier`：已完成 Docker `mysql8`（3307 端口）`wety_demo` 建库与 13 张表初始化。
- `module-migrator`：已完成 `module/**` 64 个文件的 Jakarta EE 与 OpenAPI 3 注解迁移，`src/test/**` 确认已兼容 JUnit 5；正在补充 `SM2UtilsTest` 单测。
- `core-common`：已完成 `core/**` 与 `common/**` 44 个文件的 Jakarta EE、OpenAPI 3 / Knife4j 4、Spring 6 / Hibernate 6 改造；在首轮编译报 `SM2Utils.java` 未捕获 `ScriptException` 后，已捕获并包装为 `CustomException`（保留 cause）。
- `verifier` 首轮全面验证通过：
  - `mvn -f wety-demo-api/pom.xml clean package`：`BUILD SUCCESS`，162 主源码 + 5 测试源码编译通过，`Tests run: 26, Failures: 0, Errors: 0, Skipped: 0`。
  - JDK 21 实机启动 `backend-1.0.0.jar`（`dev` profile，3307 端口 MySQL 8）：`Started Application in 3.515 seconds`，0 条启动期 ERROR；`GET /bac/common/getVerifyImage`、`GET /v3/api-docs/swagger-config`、`GET /v3/api-docs`、`GET /doc.html` 均返回 HTTP 200 且内容正常；临时进程已优雅停止、6676 端口已释放。
  - 静态扫描：`javax.{servlet,persistence,annotation,validation}` 与 `io.swagger.annotations` / `springfox` 均 0 命中。

### build-config 补充修复（JDK 21 SM2 Nashorn 兼容）
- [x] 在 `wety-demo-api/pom.xml` 中新增 `org.openjdk.nashorn:nashorn-core:15.6`（附中文说明注释），解决 JDK 15+ 移除内置 Nashorn 导致 `sm-crypto` 运行期缺失 JS 引擎的风险。
- [x] 执行 `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home mvn -f wety-demo-api/pom.xml dependency:resolve`，确认 `org.openjdk.nashorn:nashorn-core:jar:15.6` 正常解析（`BUILD SUCCESS`）。
- [x] 同步更新工作区过程文档 `task_plan.md`、`findings.md`、`progress.md`。
