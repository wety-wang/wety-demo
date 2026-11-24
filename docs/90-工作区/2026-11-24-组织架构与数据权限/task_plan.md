# 任务计划：组织架构与数据权限升级（阶段 1：架构与数据层）

## 目标

完成“组织架构与数据权限”升级第一阶段的架构设计、API 契约、数据库脚本、JPA 实体与 Repository 以及数据权限核心引擎，为后续后端与前端并行开发提供唯一、无歧义的基线。

## 执行步骤

1. **文档与 API 契约**
   - 创建工作区三件套（`task_plan.md`、`findings.md`、`progress.md`）并更新 `docs/90-工作区/README.md`。
   - 编写正式设计文档 `docs/01-组织架构与数据权限/02-设计/组织架构与数据权限设计方案.md`，定义数据模型、权限码体系、数据权限引擎设计及完整前后端 API 契约。
   - 更新 `docs/README.md` 需求索引。
2. **数据库脚本**
   - 更新全量初始化脚本 `wety-demo-api/deploy/sql/init-db.sql`。
   - 新增增量迁移脚本 `wety-demo-api/deploy/sql/db-migrate-org-datascope.sql`。
3. **Entity 与 Repository**
   - 新增 `DeptEntity`、`PositionEntity`、`AccountDeptLinkEntity`、`AccountPositionLinkEntity`、`RoleDeptLinkEntity` 及其 Repository。
   - 改造 `AccountEntity`、`RoleEntity`、`LoginAuditLogEntity`、`OperationAuditLogEntity` 及其 Repository，保持现有代码编译兼容。
4. **数据权限核心引擎**
   - 在 `com.wety.demo.core.datascope` 实现 `DataScopeEnum`、`@DataScope`、`DataScopeContext`、`DataScopeResolver`、`DataScopeAspect`、`DataScopeChecker`。
   - 增强 `AppThreadLocal` 与 `BaseRepositoryImpl`。
5. **编译验证**
   - 执行 `cd wety-demo-api && mvn test-compile`，确认 Java 8 下编译通过无错误。
   - 更新 `progress.md` 并向主控汇报。

## 验收标准

- 设计方案中的 API 契约覆盖全部部门、岗位、用户、角色与审计接口，字段与权限码清晰无歧义。
- SQL 脚本符合 `AGENTS.md` 规范（软删除、无外键、完整 COMMENT 与索引）。
- 实体类遵循 Java 8、无 Lombok、手写 getter/setter、中文 Javadoc。
- 数据权限引擎支持多角色并集、超管放行、嵌套上下文安全恢复及水平越权校验。
- `mvn test-compile` 全部通过。
