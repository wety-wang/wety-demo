# 调研记录：组织架构与数据权限升级

## 1. 现有代码与架构现状

- **技术栈**：Spring Boot 2.3.11.RELEASE、Java 8、Spring Data JPA、MySQL 8、Knife4j 2.0.5，项目未使用 Lombok，所有 Entity/DTO/VO 均手写 getter/setter。
- **JPA 动态查询**：`Application` 配置了 `@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class, basePackages = { "com.wety.demo" })`，所有 Repository 底层实际均由 `BaseRepositoryImpl` 实例化，通过 `CriteriaQueryObject` + `@CriteriaExpression` 构造 `Specification`。
- **切面支持**：`com.wety.demo.core.aop.UserOperationLogAop` 已使用 `@Aspect`，`spring-boot-starter-data-jpa` 已传递引入 AspectJ 依赖，可直接编写 `DataScopeAspect`。
- **线程上下文**：`AppThreadLocal` 为单例 `ThreadLocal<Map<String, Object>>`，通过 `AppThreadLocalCleanFilter` 在请求结束时清理。已保存 `CURRENT_ACCOUNT`、`CLIENT_IP`、`RETURN_CODE`、`RETURN_MESSAGE` 等。
- **异常处理**：`GlobalHandlerExceptionResolver` 读取 `AppThreadLocal` 中的 `RETURN_CODE` / `RETURN_MESSAGE` 并封装为 JSON 响应；`GlobalReturnCodeEnum.ACCESS_DENIED` 对应 `"402"`。
- **现有权限码**：
  - `101-00-00` 原“用户管理”（`101-01-00` 用户管理、`101-02-00` 角色管理）
  - `102-00-00` 审计日志（`102-01-00` 登录日志、`102-02-00` 操作日志）

## 2. 关键设计决策与边界处理

1. **多部门与单部门兼容**：底层使用 `account_dept_link`（含 `primary_flag`、`leader_flag`）支持多部门，同时 `account` 表冗余 `primary_dept_code` 便于列表展示及 `BaseRepositoryImpl` 直接通过单表字段做数据权限过滤。
2. **部门物化路径格式**：根部门 `dept-root` 的 `parent_code = '0'`、`ancestors = '0,'`；其子部门 `ancestors = '0,dept-root,'`。查询某部门 `X` 的所有子孙部门时，使用 `ancestors LIKE '%,X,%'`（JPA `Containing` 传 `"," + deptCode + ","`），确保精确匹配不会因编码前缀相似而误匹配。
3. **数据权限多角色并集**：
   - 超管（`account.superAdmin == 1` 或兼容存量 `type == "admin"`）直接返回 `all = true`。
   - 任一有效角色 `dataScope == 1 (ALL)` 直接返回 `all = true`。
   - 否则合并所有有效角色的 `CUSTOM`、`DEPT`、`DEPT_AND_CHILD` 部门集合，以及 `SELF` 标志。
   - 若用户无任何有效角色，默认按最小权限 `SELF` 处理。
4. **BaseRepositoryImpl 注入安全性**：
   - 当线程存在激活且非 `all` 的 `DataScopeContext` 时，先检测当前查询实体的 JPA Metamodel 是否包含 `deptField` 或 `userField`。
   - 若实体两者皆不含（例如 `@DataScope` 标注的方法内部附带查询其他无关辅助表），则跳过注入，避免误杀；
   - 若实体至少包含其一，则严格按 `deptField IN (...) OR userField = currentUserCode` 拼装；若可访问条件为空，则注入 `cb.disjunction()`（恒假）防止越权泄露。
