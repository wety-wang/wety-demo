# 进度记录：组织架构与数据权限升级（全阶段收口）

## 阶段 1：设计方案、数据模型与数据权限引擎（已完成）

- [x] 完成现有代码库、SQL 脚本、JPA 基础设施、权限体系与异常机制调研。
- [x] 创建工作区过程三件套（`task_plan.md`、`findings.md`、`progress.md`）并更新 `docs/90-工作区/README.md`。
- [x] 编写正式设计方案与前后端 API 契约文档 `docs/01-组织架构与数据权限/02-设计/组织架构与数据权限设计方案.md` 并更新 `docs/README.md`。
- [x] 更新 `wety-demo-api/deploy/sql/db-init-ddl.sql` 与新增增量迁移脚本 `wety-demo-api/deploy/sql/db-migrate-org-datascope.sql`。
- [x] 新增 5 组组织架构/数据权限 Entity 与 Repository，改造 4 个存量 Entity 并增强 3 个存量 Repository。
- [x] 在 `com.wety.demo.core.datascope` 实现数据权限核心引擎（`DataScopeEnum`、`@DataScope`、`DataScopeContext`、`DataScopeResolver`、`DataScopeAspect`、`DataScopeChecker`），并增强 `AppThreadLocal` 与 `BaseRepositoryImpl`。

## 阶段 2：组织架构后端接口与前端页面（已完成）

- [x] 完成部门管理（`DeptController`/`DeptService`）、岗位管理（`PositionController`/`PositionService`）、用户管理（`OrgUserController`/`OrgUserService`）全套 CRUD 与防越权校验。
- [x] 完成角色管理迁移至 `/bac/org/role` 及数据权限配置接口（`queryDataScope`/`updateDataScope`），移除旧 `UserManageController`/`UserManageService`。
- [x] 完成登录日志与操作日志数据权限注解接入及 `deptCode`/`deptName` 落库与回填；更新 `LoginService`、`UserService`、`AuthenticationCheckerImpl`、`AuthorizationCheckerImpl`。
- [x] 完成前端路由/菜单升级为“组织架构”（部门、岗位、用户、角色）及对应 API 与页面，移除旧 `user-manage` 目录。

## 阶段 3：质量保障、缺陷修复、单元测试与全量验证（已完成）

### 1. 关键缺陷排查与修复
- [x] **修复 `RequestMappingPermissionScanner` 无法识别 `@PostMapping`/`@GetMapping` 的启动阻断缺陷**：改为使用 Spring `AnnotatedElementUtils.findMergedAnnotation(..., RequestMapping.class)`，同时统一优化 `RequestMappingNoLoginScanner`，确保所有控制器路由均能被权限缓存与免登录缓存正确扫描。
- [x] **修复 `wety-demo-api/pom.xml` 中 `maven-surefire-plugin` 硬编码 `<skipTests>true</skipTests>`**：移除硬编码配置，使 `mvn test -DskipTests=false` 能真正执行单元测试。
- [x] **前后端契约逐项核对与补齐**：
  - 核对 `/bac/org/{dept,position,user,role}`、`/bac/common/queryAccount`、`/bac/log/{loginLog,operationLog}` 全部接口 URL、HTTP 方法、请求 DTO 与响应 VO 字段。
  - 修正 `wety-demo-view/src/api/common/user.ts` 中 `AccountDTO.roleNames` 类型为 `string`（与后端 `AccountDetailVO` 保持一致），并补齐 `email`/`phoneNumber`/`type` 可选字段。
  - 为 `wety-demo-view/src/api/audit-log/{login,operation}.ts` 补充 `deptCode`/`deptName` 字段，并在 `src/pages/audit-log/login-log/index.vue` 与 `operation-log/index.vue` 表格中补充“所属部门”展示列。

### 2. 新增核心单元测试（26 个用例 100% PASS）
- [x] `RequestMappingPermissionScannerTest`（1 用例）：回归验证 `RoleController`（`@PostMapping`）、`DeptController`/`PositionController`/`OrgUserController` 及审计日志控制器权限路由全量扫描。
- [x] `DataScopeResolverTest`（7 用例）：覆盖内置超管、存量 `type=admin`、多角色 ALL 优先、CUSTOM + DEPT 并集、DEPT_AND_CHILD 经 `ancestors` 展开、无角色回退 SELF、空账号兜底。
- [x] `DataScopeCheckerTest`（4 用例）：覆盖全量放行、部门匹配放行、本人匹配放行、越权抛出 402 `CustomException` 并设置返回码。
- [x] `DeptServiceTest`（6 用例）：覆盖新增部门 `ancestors` 计算、父部门设为自身/子孙节点防成环、父级移动级联刷新多级子孙 `ancestors`、存在子部门/用户时禁止删除。
- [x] `OrgUserServiceTest`（8 用例）：覆盖新增用户防超管提权、禁止删/禁内置超管、非超管禁止修改/重置超管、禁止删/禁当前登录人自己、水平越权被 `DataScopeChecker` 拦截。

### 3. 全量验证命令与结果
- 后端编译与单元测试：`cd wety-demo-api && mvn test -DskipTests=false` -> **BUILD SUCCESS**（162 个主源文件 + 5 个测试源文件编译通过；`Tests run: 26, Failures: 0, Errors: 0, Skipped: 0`）。
- 前端类型检查与生产构建：`cd wety-demo-view && npm run typecheck && npm run build` -> **PASS**（`vue-tsc --noEmit` 零错误，`vite build` 打包成功，产物 6.12 MB）。
- 规范与工作区核查：仓库根目录不存在 `task_plan.md`/`findings.md`/`progress.md`，过程三件套均位于 `docs/90-工作区/2026-11-24-组织架构与数据权限/`，无敏感信息或调试代码残留。

### 4. 仍需人工关注的风险与上线事项
1. **数据库迁移执行**：存量环境上线前须在目标 MySQL 库执行 `wety-demo-api/deploy/sql/db-migrate-org-datascope.sql`（新库使用 `db-init-ddl.sql`），并核对默认根部门 `dept-root` 与内置超管 `admin` 的 `super_admin = 1`、`primary_dept_code = 'dept-root'` 初始化状态。
2. **鉴权开关核查**：确认生产环境配置 `application.authorization.switch=true`（`AuthorizationFilter` 生效条件），避免开发配置误带入生产导致接口鉴权未开启。
3. **运行环境 JDK 版本**：项目后端目标为 Java 8，`com.antherd:sm-crypto:0.3.2` 依赖 JDK 8 内置 Nashorn 引擎；若未来升级至 JDK 15+，需替换 SM2 实现（如 BouncyCastle）避免 `ScriptEngine` 为空。
4. **历史权限码数据**：既有角色若绑定了旧版 `101-xx-xx`（旧“用户管理”）权限码，升级后需在“组织架构 -> 角色管理”重新核对并授予新版菜单权限码及数据权限范围。
