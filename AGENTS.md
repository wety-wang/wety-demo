# AGENTS.md

本文件用于指导 Codex/ChatGPT/Gemini 等 VibeCoding 工具在本仓库中进行代码分析、开发、重构、测试和代码审查。

## 1. 适用范围与执行原则

- 本文件放置于仓库根目录，对整个仓库生效。
- 子目录存在更具体的 `AGENTS.md` 时，优先遵循距离目标文件更近的规则。
- 开始修改前，先阅读目标模块现有代码、配置、测试和相邻实现，优先复用已有模式，不得凭空引入另一套架构。
- 使用中文进行分析、说明、代码注释和最终回复；代码标识符遵循项目既有英文命名规范。
- 仅修改完成当前任务所必需的文件，不进行与任务无关的格式化、重命名或重构。
- 未经明确要求，不新增生产依赖、不升级框架版本、不修改公共接口兼容性、不执行破坏性数据库操作。
- 不得提交密钥、Token、密码、证书、个人信息或真实生产配置；示例统一使用占位值。
- 发现需求与现有实现、本文件规则或数据库结构冲突时，必须明确指出冲突，不得静默猜测或绕过。
- 对复杂、跨模块或高风险任务，先形成简要实施计划，再开始修改。

## 2. 标准工作流程

1. 确认目标模块、需求边界和验收标准。
2. 阅读目标目录及父目录中的 `AGENTS.md`、`AGENTS.override.md`、README、配置和相邻代码。
3. 搜索是否已有可复用的 Entity、DTO、VO、Service、DAO、转换方法、异常类型和前端组件。
4. 对复杂任务先列出实施步骤、影响范围、数据迁移和测试方案。
5. 采用最小改动完成需求，保持已有接口和数据兼容性。
6. 补充或更新必要测试，并执行与改动相关的编译、测试、Lint、类型检查和构建。
7. 检查最终 diff，确认没有无关修改、调试代码、敏感信息、死代码或未处理异常。
8. 最终回复必须说明：修改内容、涉及文件、执行的验证命令、验证结果以及仍存在的风险。

### 2.1 项目文档存储规范

- 项目正式文档统一存放在 `docs/`，按“需求编号 → 生命周期分类 → 具体文件”组织，禁止按文档类型在顶层平铺。
- 跨需求且长期有效的资料放在 `docs/00-公共资料/`，包括架构、运维、规范、模板和 ADR；不得在多个需求目录重复维护同一份公共资料。
- 每个独立需求使用固定且不复用的编号目录：`docs/<NN-需求主题>/`，例如 `docs/03-分销商默认产品/`；禁止在 `docs/` 顶层新增无编号的散落目录（如英文临时专题目录）。
- 需求目录内按生命周期建立子目录；未产生内容的目录无需预建：

```text
01-需求/      # 业务目标、范围、角色、验收标准
02-设计/      # 架构、接口、数据、页面和技术约束
03-实施/      # 实施计划、迁移、风险和回滚方案
04-验收/      # 测试结果、发布记录、遗留问题和验收结论
99-归档/      # 已被替代的历史文档
```

- 当重构或新版方案替代历史方案时，必须在主索引与需求目录说明中明确唯一生效基线，并将已废弃旧文档移入 `99-归档/` 或在 Git 打标签留底后清理，避免新旧方案并存误导开发与 AI 上下文。
- VibeCoding 工具的计划、调研、过程记录等临时产物放在 `docs/90-工作区/<YYYY-MM-DD-主题>/`；只有经确认、需要长期复用的结论，才转写到对应需求的正式分类目录或 `00-公共资料`。
- 历史月份已完结的工作区目录应按月归档至 `docs/90-工作区/archive/YYYY-MM/`，仅在顶层保留当月活跃目录。
- 所有需求、开发、重构、修复和审查任务，只要产生过程记录，必须在对应的工作区目录中创建 `task_plan.md`、`findings.md` 和 `progress.md` 三个文件；即使使用 planning-with-files 技能，也必须遵循该目录规则。
- 禁止在仓库根目录创建 `task_plan.md`、`findings.md` 或 `progress.md`。任务结束前必须检查三个文件均位于对应的 `docs/90-工作区/<YYYY-MM-DD-主题>/` 目录，且仓库根目录不存在同名文件，并在最终回复中列出文件位置。
- 双索引解耦更新：新建、修改或移动正式需求/公共文档（`00-*`～`89-*`）时更新 `docs/README.md`；新建、更新或归档 VibeCoding 工具过程记录时只更新 `docs/90-工作区/README.md`（不向 `docs/README.md` 追加历史过程流水账）。

## 3. 后端分层规范

新增业务模块遵循：

```text
Entity -> DAO -> Service -> Controller
                 |          |
                 |          -> DTO / VO
                 -> 业务规则、事务、对象转换
```

- **Entity**：JPA 实体，位于 `com.qust.smp.module.{模块}.entity`。
- **DAO**：Spring Data JPA 接口，位于 `com.qust.smp.module.{模块}.dao`。
- **Service**：业务逻辑，位于 `com.qust.smp.module.{模块}.service`。
- **Controller**：接口适配层，位于 `com.qust.smp.module.{模块}.controller`。
- **DTO**：请求和服务入参模型，位于 `com.qust.smp.module.{模块}.dto`。
- **VO**：接口返回模型，位于 `com.qust.smp.module.{模块}.vo`。

强制要求：

- Controller 不得直接访问 DAO。
- Controller 不得编写复杂业务逻辑，只负责参数绑定、校验、调用 Service 和返回 VO。
- Service 负责事务边界、业务校验、状态变更和 Entity/DTO/VO 转换协调。
- DAO 只负责持久化访问，不承载业务规则。
- 不得直接把 Entity 暴露给前端。

## 4. Controller 接口规范（强制）

### 4.1 接口映射注解及 HTTP 方法限制

Controller 接口只允许使用以下三种 Spring MVC 映射注解：

- `@RequestMapping`
- `@GetMapping`
- `@PostMapping`

强制要求：

- 禁止使用 `@PutMapping`、`@DeleteMapping`、`@PatchMapping` 等其他映射注解。
- 业务接口只允许使用 GET 和 POST 请求，不得通过 `@RequestMapping` 变相声明 PUT、DELETE、PATCH 等请求方法。
- 类级别统一使用 `@RequestMapping(value = "...")` 声明模块路径。
- 方法级别优先使用 `@GetMapping(value = "...")` 或 `@PostMapping(value = "...")`。
- 查询详情等简单只读接口可以使用 GET；复杂查询、分页查询及包含复杂条件的查询使用 POST。
- 新增、修改、删除、批量操作、状态变更和其他业务动作统一使用 POST。
- 所有映射注解都必须显式填写 `value` 属性，不得使用省略 `value` 的简写形式。
- 不得只写 `@RequestMapping(value = "...")` 而不限定请求方法来承载方法级业务接口；确需在方法级使用 `@RequestMapping` 时，只能显式指定 `RequestMethod.GET` 或 `RequestMethod.POST`。

```java
// 正确：类级路径
@RequestMapping(value = "/bac/product")

// 正确：查询接口
@GetMapping(value = "/get")
@PostMapping(value = "/list")

// 正确：写操作全部使用 POST
@PostMapping(value = "/add")
@PostMapping(value = "/update")
@PostMapping(value = "/delete")
@PostMapping(value = "/update-status")

// 禁止：省略 value
@RequestMapping("/bac/product")
@GetMapping("/get")
@PostMapping("/list")

// 禁止：使用其他映射注解
@PutMapping(value = "/update")
@DeleteMapping(value = "/delete")
@PatchMapping(value = "/update-status")

// 禁止：通过 RequestMapping 使用非 GET/POST 请求
@RequestMapping(value = "/update", method = RequestMethod.PUT)
@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
```

### 4.2 请求必须使用 DTO，返回必须使用 VO

- Controller 的业务请求数据必须使用 DTO 接收。
- Controller 的业务返回数据必须使用 VO 返回。
- 禁止使用 `Map`、`HashMap`、`JSONObject`、原始 `Object` 作为 Controller 请求或返回模型。
- 禁止使用 Entity 直接接收请求或直接返回给前端。
- 列表返回使用 `List<XxxVo>` 或项目统一的 `PageResult<XxxVo>`。
- 新增、更新、删除等操作也应返回明确的 VO，例如 `XxxOperationVo`、`XxxDetailVo`，不得用临时 Map 拼装结果。
- 文件下载、流式输出等无法使用普通 VO 表达的接口，可以使用 `ResponseEntity<Resource>`、`StreamingResponseBody` 等明确类型，但必须在方法注释中说明原因，且仍禁止使用 Map。

### 4.3 参数绑定规则

- 禁止使用 `@PathVariable`。
- POST 类请求使用 `@Valid @RequestBody XxxDto`。
- GET 查询参数需要封装为 DTO 时，使用 `@Valid @ModelAttribute XxxQueryDto`。
- 复杂列表查询统一使用 POST，并通过 `@RequestBody` 接收查询 DTO。
- 所有外部输入必须进行 Bean Validation 校验。

### 4.4 Controller 示例

```java
/**
 * 产品管理接口。
 */
@RestController
@RequestMapping(value = "/bac/product")
@RequiredArgsConstructor
public class ProductController {

    /** 产品服务。 */
    private final ProductService productService;

    /**
     * 分页查询产品。
     *
     * @param dto 查询条件
     * @return 产品分页数据
     */
    @PostMapping(value = "/list")
    public PageResult<ProductListVo> list(
            @Valid @RequestBody ProductQueryDto dto) {
        return productService.query(dto);
    }

    /**
     * 查询产品详情。
     *
     * @param dto 产品查询参数
     * @return 产品详情
     */
    @GetMapping(value = "/get")
    public ProductDetailVo get(
            @Valid @ModelAttribute ProductDetailQueryDto dto) {
        return productService.get(dto);
    }

    /**
     * 新增产品。
     *
     * @param dto 新增参数
     * @return 操作结果
     */
    @PostMapping(value = "/add")
    public ProductOperationVo add(
            @Valid @RequestBody ProductAddDto dto) {
        return productService.addProduct(dto);
    }
}
```

统一响应包装由 `GlobalResponseAdvice` 处理，Controller 仅返回业务 VO，不得重复手工包装统一响应对象，除非现有接口明确要求。

## 5. 命名规范

### 5.1 类命名

- Entity：以 `Entity` 结尾，例如 `ProductEntity`。
- DAO：以 `Dao` 结尾，例如 `ProductDao`。
- Service：以 `Service` 结尾，例如 `ProductService`。
- Controller：以 `Controller` 结尾，例如 `ProductController`。
- DTO：以 `Dto` 结尾，例如 `ProductQueryDto`、`ProductAddDto`。
- VO：以 `Vo` 结尾，例如 `ProductListVo`、`ProductDetailVo`。
- 枚举：以 `Enum` 结尾，例如 `ProductStatusEnum`；枚举值使用全大写加下划线。

### 5.2 方法命名

- Controller：`list`、`get`、`add`、`update`、`delete`。
- Service 查询：`query`、`get`、`findXxx`。
- Service 写操作：`addXxx`、`updateXxx`、`deleteXxx`、业务动作动词。
- DAO：`findByXxx`、`existsByXxx`、`countByXxx`、`deleteByXxx`。

方法名称必须表达真实业务含义。复杂业务动作不得全部使用模糊的 `handle`、`process`、`execute`。

### 5.3 变量与常量

- 变量使用小驼峰命名，含义清晰，禁止无意义缩写。
- 常量使用全大写加下划线，例如 `DEFAULT_PAGE_SIZE`。
- 布尔字段使用状态或过去式名称，例如 `deleted`、`approved`、`activated`，字段名禁止 `is` 前缀。
- JavaBean getter 可按框架约定生成，例如 `isDeleted()`；“禁止 `is` 前缀”仅针对字段和数据库列命名。
- 禁止魔法数字和魔法字符串；应使用常量或枚举表达业务含义。

## 6. Service 与事务规范

- Service 类默认使用 `@Transactional(readOnly = true)`。
- 新增、更新、删除等写操作方法使用：

```java
@Transactional(rollbackFor = Exception.class)
```

- 事务边界应尽可能小，不得在数据库事务中执行大模型调用、远程 HTTP 请求、文件上传、长时间计算或其他不可控耗时操作。
- 写操作必须先完成业务合法性校验，再执行状态变更和持久化。
- 多步骤状态变更必须保证原子性，并清楚处理重复请求、并发更新和幂等性。
- 不得捕获异常后静默忽略；需要转换异常时，必须保留原始异常作为 cause。

## 7. DAO 与 JPA 规范

### 7.1 DAO

- 必须使用参数化查询，禁止拼接 SQL 或 JPQL。
- 查询方法返回类型必须明确，避免滥用原始 `Object[]`；复杂结果应使用投影、DTO 或明确接口。
- 大数据量查询必须分页，禁止无条件全表加载。
- 避免 N+1 查询，根据场景使用 `JOIN FETCH`、`@EntityGraph` 或批量查询。
- 批量操作设置合理 batch size，执行后按需要调用 `flush()`。

```java
@Query("SELECT p FROM ProductEntity p WHERE p.code = :code AND p.deleted = false")
Optional<ProductEntity> findAvailableByCode(@Param("code") String code);
```

### 7.2 Entity

- Entity 至少使用 `@Entity` 和 `@Table`。
- `@DynamicInsert`、`@DynamicUpdate` 仅在确有动态 SQL 或数据库默认值需求时使用，不得机械添加到所有实体。
- 主键使用 `Long id`，并配置：

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

- 新代码优先使用 `LocalDateTime`；仅在实体字段使用旧版 `Date` 或 `Calendar` 时使用 `@Temporal`。
- Entity 之间原则上不建立复杂双向对象关联；表关联关系按项目约定通过业务 code 维护。
- 所有正常业务查询必须过滤软删除数据。

## 8. DTO、VO 与对象转换规范

### 8.1 DTO

- DTO 仅承载请求数据和服务入参，不包含持久化逻辑。
- 根据接口用途拆分 `QueryDto`、`AddDto`、`UpdateDto`、`DeleteDto`，不得用一个万能 DTO 覆盖所有场景。
- 使用 Bean Validation 描述边界约束。

```java
public class ProductAddDto {

    /** 产品名称。 */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称长度不能超过100")
    private String name;

    /** 产品数量。 */
    @NotNull(message = "产品数量不能为空")
    @Min(value = 0, message = "产品数量不能小于0")
    private Integer count;
}
```

### 8.2 VO

- VO 只暴露前端真正需要的字段。
- VO 不得包含 JPA 懒加载代理、内部主键、密码、Token、密钥、内部异常堆栈等敏感或内部字段。
- 金额、时间、状态等字段的格式必须与项目现有接口保持一致。
- 不得通过 Map 临时拼装 VO；应创建命名明确的 VO 类。

### 8.3 对象转换

- 优先复用项目已有转换器、构造方法或工厂方法。
- 转换逻辑较多时，提取为独立 Converter/Assembler，不得在 Controller 中逐字段拼装。
- 使用反射式复制工具前必须检查字段语义和类型，禁止因字段同名而盲目复制敏感字段或内部字段。

## 9. 代码逻辑与注释规范（强制）

### 9.1 逻辑清晰

- 每个方法只负责一个清晰职责。
- 优先使用提前返回和守卫语句，避免多层嵌套。
- 业务步骤应按“校验 -> 查询 -> 计算/决策 -> 持久化 -> 返回”组织。
- 复杂条件必须提取为含义明确的方法、策略或规则对象。
- 重复逻辑必须抽取复用，但不得为一次性简单逻辑过度设计。
- 不得保留注释掉的大段旧代码、临时调试代码、无效分支或未使用变量。
- 对状态流转、金额计算、权限判断、分佣规则、异步任务等关键业务逻辑，应显式表达前置条件、计算依据和结果。

### 9.2 注释齐全且有意义

- 所有类、公共方法、DTO/VO/Entity 业务字段必须添加 Javadoc。
- 复杂私有方法、关键业务分支、非显而易见的算法、兼容性处理和异常兜底必须添加注释。
- 方法 Javadoc 至少说明用途、参数、返回值；可能抛出明确异常时说明 `@throws`。
- 注释重点解释“为什么这样做、业务规则是什么、边界条件是什么”，不得只把代码翻译成中文。
- 修改代码后必须同步更新相关注释，禁止保留与实现不一致的过期注释。
- getter、setter、构造器等机械代码若由 Lombok 生成，无需编写无意义注释。
- TODO 必须写明待办原因和处理条件，不得只写裸 `TODO`。

```java
/**
 * 根据当前分销关系计算本次订单应生成的佣金明细。
 *
 * <p>佣金必须以订单支付时保存的关系快照为依据，避免后续关系变更影响历史账务。</p>
 *
 * @param orderCode 订单业务编码
 * @return 佣金明细列表
 * @throws BusinessException 订单不存在或关系快照不完整时抛出
 */
public List<CommissionDetailVo> calculateCommission(String orderCode) {
    // 先读取支付时快照，不能直接读取当前关系，否则历史结果可能发生变化。
    // ...
}
```

## 10. 日志与异常规范

- 使用参数化日志，禁止字符串直接拼接。
- ERROR：系统异常、不可恢复错误，必须记录异常对象以保留堆栈。
- WARN：可恢复异常、业务风险、降级处理。
- INFO：关键业务动作和重要状态变更。
- DEBUG：详细过程和排查信息。
- 不得记录密码、验证码、Token、Authorization、完整身份证号、银行卡号或其他敏感数据。
- DTO 包含敏感字段时，不得直接整体序列化输出；必须脱敏或只记录必要字段。
- 若已有 AOP 接口日志和 `@OperationLog`，不得在每个 Controller 重复记录同一请求日志。
- 业务异常使用项目统一异常类型和错误码，由 `GlobalExceptionHandler` 处理。

```java
log.info("开始更新产品，productCode: {}", dto.getCode());
log.error("更新产品失败，productCode: {}", dto.getCode(), exception);
```

## 11. 安全规范

- Controller 入参必须执行 Bean Validation。
- Service 必须校验当前用户、租户、数据归属和操作权限，不能只依赖前端传参。
- 免登录接口使用 `@NoLogin`，权限控制使用 `@Permission`，操作审计使用 `@OperationLog`。
- 认证方式为 JWT Bearer Token。
- 禁止字符串拼接 SQL、JPQL 或原生查询条件。
- 文件上传必须校验文件类型、大小、扩展名、存储路径和访问权限。
- 文件路径必须防止目录穿越，禁止直接信任用户传入路径。
- 外部 API、AI 返回内容和反序列化数据都视为不可信输入，使用前必须校验。
- 错误响应不得暴露堆栈、SQL、服务器路径、内部 IP、密钥或框架敏感信息。

## 12. 数据库设计规范

### 12.1 核心原则

- `id`：数据库自增主键，仅用于数据库内部，不参与对外业务逻辑。
- `code`：业务唯一标识，类型统一为 `VARCHAR(40)`。
- 所有增删改查优先以业务 `code` 为依据。
- 使用 `deleted` 字段实现软删除，禁止业务代码执行物理删除。
- 禁止数据库外键约束，关联完整性由业务代码维护。
- 禁止使用视图、触发器、存储过程和自定义数据库函数承载业务逻辑。
- 每张业务表至少包含：`id`、`code`、`create_time`、`update_time`、`deleted`。
- 新增表和字段必须添加 COMMENT。

### 12.2 命名与类型

- 表名、字段名使用全小写加下划线。
- 时间字段使用 `_time` 后缀。
- 状态字段使用 `_status` 或 `_state` 后缀。
- 布尔字段使用 `TINYINT(1)`，字段名不得使用 `is_` 前缀。
- 金额使用 `DECIMAL(P,S)`，禁止使用浮点类型。
- 枚举字段使用整数类型，并在 COMMENT 中完整说明取值。

### 12.3 索引

- 业务唯一字段添加唯一索引：`uk_表名_字段名`。
- 常用查询字段添加普通索引：`idx_表名_字段名`。
- 复合索引按实际查询的最左前缀设计，不得为所有字段机械建索引。
- 软删除条件高频出现时，结合查询模式评估是否加入复合索引。

### 12.4 标准表结构示例

```sql
CREATE TABLE xxx_template (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '数据库主键',
    code        VARCHAR(40) NOT NULL COMMENT '业务唯一标识',

    -- 业务字段
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理，1-已完成',

    -- 审计字段。既然保存的是业务 code，类型必须与 code 一致。
    create_user VARCHAR(40) DEFAULT NULL COMMENT '创建人业务编码',
    update_user VARCHAR(40) DEFAULT NULL COMMENT '更新人业务编码',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',

    UNIQUE KEY uk_xxx_template_code (code)
) COMMENT='XXX模板表';
```

- `CHECK` 约束仅在目标 MySQL 版本能够可靠执行时使用，同时保留应用层校验。
- 数据库结构变更必须提供可审查的 SQL 或迁移脚本。
- 未经明确授权，不得执行 `DROP`、`TRUNCATE`、全表 `DELETE` 或不可逆数据更新。

### 12.5 SQL 脚本存储与变更规范

- 所有数据库初始化、结构变更、数据迁移及回滚 SQL 脚本统一存放在 `backend/deploy/sql/` 目录下，禁止散落在业务模块目录或其他位置。
- 目录组织与命名约定：
    - **全量初始化脚本**：`backend/deploy/sql/init-db.sql`，新环境首次建库建表使用（字符集统一为 `utf8mb4`、`utf8mb4_general_ci`）。
    - **增量升级脚本**：`backend/deploy/sql/upgrade-v<序号>-<简短主题>.sql`（例如 `upgrade-v1-init-maintenance.sql`、`upgrade-v2-device-alarm.sql`），按版本序号递增，禁止修改已上线执行的旧升级脚本。
    - **回滚脚本**：`backend/deploy/sql/rollback-v<序号>-<简短主题>.sql`，涉及破坏性或高风险变更时与对应 `upgrade-*` 脚本成对提供。
    - **SQL 验证脚本**：`backend/deploy/sql/tests/`（按需存放迁移校验或回归 SQL）。
      - 每次新增或变更表结构（包括调整字段、索引、字典/初始化数据）时，必须同步在 `backend/deploy/sql/` 下补充对应的 `upgrade-v*` 脚本，并视需要同步更新 `backend/deploy/sql/init-db.sql` 基线；脚本需保证幂等性或明确前置版本，禁止仅依赖 Hibernate `ddl-auto: update` 自动改表而遗漏 SQL 沉淀。

## 13. 性能规范

- 大数据量查询必须分页，禁止无条件全表扫描和全量加载。
- 避免循环内逐条访问数据库。
- 批量写入应使用批处理，并控制单批数量。
- 缓存仅用于相对稳定、体积可控的数据；禁止缓存超大集合和敏感信息。
- 缓存必须明确 key、过期策略、失效策略和多租户隔离方式。
- 外部 API 和 AI 调用必须配置超时、错误处理和必要的降级策略。
- 异步任务必须可追踪状态、失败原因和重试边界，避免无限重试。

## 14. 前端编码规范

- 使用 Vue 3 Composition API，并遵循现有组件组织方式。
- 页面组件不得直接散落 Axios 请求；统一通过 `src/services/` 调用后端。
- 跨组件或跨页面状态统一放入 Pinia Store；局部状态保留在组件内部。
- 优先复用现有组件、类型、工具方法和样式变量，不得重复造轮子。
- 接口 DTO/VO 发生变化时，同步更新前端类型、服务封装和页面处理。
- 必须处理 loading、空数据、错误和重复提交状态。
- 不得在前端存储密钥、数据库凭证或服务端私密配置。
- 修改页面后至少执行对应模块的生产构建；若存在 lint、test、type-check 脚本，同时执行。

## 15. 测试规范

- 新增业务逻辑必须补充测试；修复缺陷必须优先增加可复现缺陷的回归测试。
- Service 纯业务逻辑优先使用单元测试，不应为所有简单逻辑都启动完整 Spring 容器。
- Repository/DAO 查询使用数据访问层测试或必要的集成测试。
- Controller 接口使用 MockMvc 或项目现有集成测试方式验证参数校验、权限、响应和异常。
- 测试名称必须说明场景和预期结果。
- 测试数据不得依赖真实生产数据、外部密钥或不可控第三方服务。
- 外部 API、AI、文件存储等依赖在单元测试中应使用 mock 或 stub。
- 不得通过删除测试、降低断言或永久跳过测试来让构建通过。

## 16. 完成标准（Definition of Done）

任务完成前必须检查：

- [ ] 实现与用户需求和现有业务规则一致。
- [ ] 修改范围最小，没有无关文件或无关格式化。
- [ ] Controller 仅使用 `@RequestMapping`、`@GetMapping`、`@PostMapping`，业务接口仅使用 GET/POST，且映射注解全部显式填写 `value`。
- [ ] Controller 请求使用 DTO，返回使用 VO，不存在 Map/JSONObject/Entity 暴露。
- [ ] 关键逻辑结构清晰，类、公共方法和业务字段注释完整且与实现一致。
- [ ] 参数校验、权限、租户隔离、异常和日志处理完整。
- [ ] 数据库查询无明显 N+1、全表扫描或事务边界问题。
- [ ] 若涉及表结构或初始化数据变更，已在 `backend/sql/` 补充对应 `upgrade-v*`（及必要时的 `rollback-v*`）脚本并更新 `init-db.sql`。
- [ ] 已补充或更新必要测试。
- [ ] 已执行相关编译、测试、Lint、类型检查或构建，并记录结果。
- [ ] 最终 diff 不包含敏感信息、调试代码、死代码和无关变更。
- [ ] 无法执行的验证已明确说明原因和风险，不得声称未执行的检查已通过。

## 17. 代码审查重点

进行代码审查时，优先检查以下问题：

1. 业务逻辑错误、数据一致性、重复执行和并发问题。
2. 权限绕过、租户越权、敏感信息泄露、SQL 注入和文件路径安全。
3. Controller 是否使用了禁止的映射注解或非 GET/POST 请求，以及是否违反 DTO/VO、映射 `value` 和禁止 Map 的规范。
4. 事务范围、异常吞噬、异步任务失败处理和外部调用超时。
5. N+1、全表查询、无分页、大对象加载和无效缓存。
6. 接口兼容性、数据库脚本可回滚性和历史数据影响。
7. 测试缺失、断言不足以及注释与实现不一致。

审查结论应先列出问题，按严重程度排序，并给出文件位置、影响、触发条件和建议修复方式。
