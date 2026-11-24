# 调研与发现：后端升级至 JDK 21 与 Spring Boot 3.5.16

## 1. 环境与基线现状

- **本机 JDK 21**：`/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home`（Oracle JDK `21.0.4`），Maven `3.9.16`。
- **升级前基线**：`spring-boot-starter-parent` `2.3.11.RELEASE`、`java.version` `1.8`、`mysql-connector-java` `8.0.33`、`druid-spring-boot-starter` `1.2.5`、手写 4 个 `org.apache.logging.log4j:*` `2.17.1`、`knife4j-spring-boot-starter` `2.0.5`、`commons-io` `2.7`、`commons-lang3`（引用未定义变量 `${commons-lang3.version}`）、`commons-beanutils` `1.9.4`、`fastjson` `2.0.32`、`hutool-all` `5.8.20`、`sm-crypto` `0.3.2`。
- **依赖扫描**：
  - `fastjson` 在 `wety-demo-api/src/` 中为 0 引用，可直接移除。
  - 源码中未使用 `javax.validation` / `jakarta.validation`（且 `springdoc-openapi-starter-webmvc-ui:2.8.9` 已传递引入 `spring-boot-starter-validation:3.5.16`），无需单独补加。

## 2. 依赖解析与兼容决策

经 `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home mvn -f wety-demo-api/pom.xml dependency:resolve` 实测，所有目标版本均在 Maven Central 正常解析：

| 依赖 / 配置项 | 升级前 | 升级后（实测解析版本） | 说明 |
| --- | --- | --- | --- |
| `spring-boot-starter-parent` | `2.3.11.RELEASE` | `3.5.16` | 移除冗余 `<spring-boot.version>` |
| `java.version` | `1.8` | `21` | 移除 compiler 插件手写 `<source>/<target>` |
| `mysql` 驱动 | `mysql:mysql-connector-java:8.0.33` | `com.mysql:mysql-connector-j:9.7.0` | 由 parent 托管版本 |
| Druid | `druid-spring-boot-starter:1.2.5` | `druid-spring-boot-3-starter:1.2.28` | Spring Boot 3 专用 starter，修正属性名 `druid.version` |
| 日志 | 手写 4 个 `log4j-*` `2.17.1` | `spring-boot-starter-log4j2:3.5.16`（Log4j `2.24.3`） | 在 `web` / `data-jpa` / `test` starter 中均排除 `spring-boot-starter-logging`，避免 Logback 与 Log4j2 冲突 |
| Knife4j / OpenAPI | `knife4j-spring-boot-starter:2.0.5` | `knife4j-openapi3-jakarta-spring-boot-starter:4.5.0` + `springdoc-openapi-starter-webmvc-ui:2.8.9` | 显式升级 `springdoc` 解决 Spring Boot 3.5 `ControllerAdviceBean` 兼容问题 |
| `commons-io` | `2.7` | `2.22.0` | 最新稳定 2.x |
| `commons-lang3` | `${commons-lang3.version}`（未定义） | `3.17.0` | 去掉未定义占位符，由 parent 托管 |
| `commons-beanutils` | `1.9.4` | `1.11.0` | 最新稳定版 |
| `hutool-all` | `5.8.20` | `5.8.48` | 最新可用 5.8.x |
| `fastjson` | `2.0.32` | 已移除 | 源码 0 引用 |
| `sm-crypto` | `0.3.2` | `0.3.2.1-RELEASE` | Maven Central 正常解析；方法签名新增受检异常 `javax.script.ScriptException`（已由 `SM2Utils` 捕获包装） |
| `nashorn-core` | 无（JDK 8 内置） | `org.openjdk.nashorn:nashorn-core:15.6` | JDK 15+ 已移除内置 Nashorn，`sm-crypto` 运行期依赖 `javax.script.Invocable` 执行 SM2 加解密，需显式引入独立版 Nashorn |
| Maven 插件 | 手写版本（`spring-boot` / `compiler:3.10.1` / `surefire:2.22.2`） | 全部移除手写版本 | 交由 parent 3.5.16 统一托管 |

## 3. 配置文件兼容检查

- `application.yaml`：Hibernate 6 中 SQL 参数绑定日志 logger 已由 `org.hibernate.type.descriptor.sql.BasicBinder` 变更为 `org.hibernate.orm.jdbc.bind`，已更新。
- `application-dev.yaml`：端口 `3306` → `3307`（对齐本机 Docker `mysql8` 容器），JDBC URL 增加 `&allowPublicKeyRetrieval=true`，移除已废弃且不兼容 Hibernate 6 的 `database-platform: org.hibernate.dialect.MySQL5InnoDBDialect`。
- `application-test.yaml`：JDBC URL 增加 `&allowPublicKeyRetrieval=true`，移除 `database-platform: org.hibernate.dialect.MySQL5InnoDBDialect`。
- `log4j.xml`：采用标准 Log4j2 配置（`<configuration>`、`Console`、`RollingFile`、`SizeBasedTriggeringPolicy`、`DefaultRolloverStrategy`），完全兼容 Log4j 2.24.3，无需修改。

## 4. 编译与实机联调发现

- **SM2 受检异常变更**：`sm-crypto:0.3.2.1-RELEASE` 中 `Sm2.doDecrypt/doEncrypt` 新增 `throws javax.script.ScriptException`，首轮 JDK 21 编译在 `SM2Utils.java` 报 2 处未捕获异常；已由 `core-common` 捕获并包装为 `CustomException`（保留原始 cause）。
- **JDK 21 Nashorn 引擎缺失**：`sm-crypto` 底层依赖 Nashorn JS 引擎，JDK 15+ 移除内置 Nashorn 后运行期触发登录/改密会因缺少 JS 引擎失败；已在 `pom.xml` 引入 `org.openjdk.nashorn:nashorn-core:15.6` 解决，并由 `module-migrator` 补充 `SM2UtilsTest` 单测验证往返加解密。
- **Spring Boot 3.5 实机联调**：fat jar 下 `PermissionScanListener`（改用 `PathMatchingResourcePatternResolver`）、`/v3/api-docs`（保留 `ByteArrayHttpMessageConverter` 首位避免 Base64 编码）、`/doc.html`、`/bac/common/getVerifyImage` 及 `application.swagger.switch: on` 的 `@ConditionalOnProperty` 均验证正常。
