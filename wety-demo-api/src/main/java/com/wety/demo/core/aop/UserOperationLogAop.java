package com.wety.demo.core.aop;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.constant.SymbolConst;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.auditlog.dto.OperationLogAddDTO;
import com.wety.demo.module.auditlog.service.OperationLogService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author wety
 */
@Component
@Aspect
@Order(Integer.MAX_VALUE - 1)
public class UserOperationLogAop implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(UserOperationLogAop.class);

    private ApplicationContext applicationContext;

    private static final String BEAN = "BEAN";

    /**
     * 复用 SpEL 解析器，避免每次创建造成性能开销
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @Resource
    private OperationLogService operationLogService;

    @Pointcut("@annotation(operationLog)")
    public void userOperationLogPointCut(OperationLog operationLog) {

    }

    @Around(value = "userOperationLogPointCut(operationLog)", argNames = "proceedingJoinPoint,operationLog")
    public Object userOperationLogAround(ProceedingJoinPoint proceedingJoinPoint, OperationLog operationLog) throws Throwable {
        Object result;

        String description;
        long beginTime = System.currentTimeMillis();
        try {

            description = handleTemplateDescParse(
                    proceedingJoinPoint.getArgs(),
                    ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod(),
                    operationLog
            );

        } catch (Throwable throwable) {
            description = "操作日志数据解析失败";
            logger.error(" OperationLog 数据解析失败 ", throwable);
        }

        boolean exception = true;

        try {
            result = proceedingJoinPoint.proceed();
            exception = false;
        } finally {
            try {
                OperationLogAddDTO operationLogAddDTO = new OperationLogAddDTO();
                addModelInfo(operationLogAddDTO, ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod(), proceedingJoinPoint.getSignature().getDeclaringType());
                // 从注解中记录操作类型
                operationLogAddDTO.setOperationType(operationLog.operationType().getCode());
                // 记录请求路径（若存在 Web 请求上下文）
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    HttpServletRequest req = attrs.getRequest();
                    operationLogAddDTO.setPath(req.getRequestURI());
                    // 采集更多请求元数据：HTTP方法、Query字符串、UA、Referer、请求ID
                    operationLogAddDTO.setHttpMethod(req.getMethod());
                    operationLogAddDTO.setQueryString(StringUtils.defaultString(req.getQueryString(), ""));
                    operationLogAddDTO.setUserAgent(StringUtils.defaultString(req.getHeader("User-Agent"), ""));
                    operationLogAddDTO.setReferer(StringUtils.defaultString(req.getHeader("Referer"), ""));
                    // 优先使用客户端传入的 X-Request-Id，如果没有则生成一个
                    String requestId = req.getHeader("X-Request-Id");
                    if (StringUtils.isBlank(requestId)) {
                        requestId = UUID.randomUUID().toString();
                    }
                    operationLogAddDTO.setRequestId(requestId);
                } else {
                    operationLogAddDTO.setPath("");
                    operationLogAddDTO.setHttpMethod("");
                    operationLogAddDTO.setQueryString("");
                    operationLogAddDTO.setUserAgent("");
                    operationLogAddDTO.setReferer("");
                    operationLogAddDTO.setRequestId("");
                }

                AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
                // 做 null 保护，避免未登录或上下文缺失导致 NPE
                if (currentAccount != null) {
                    operationLogAddDTO.setAccountCode(currentAccount.getCode());
                    operationLogAddDTO.setAccountLoginName(currentAccount.getLoginName());
                    operationLogAddDTO.setAccountName(currentAccount.getUserName());
                } else {
                    operationLogAddDTO.setAccountCode("-");
                    operationLogAddDTO.setAccountLoginName("-");
                    operationLogAddDTO.setAccountName("-");
                }

                operationLogAddDTO.setClientIp(AppThreadLocal.getInstance().getClientIp());

                operationLogAddDTO.setOperationTime(new Date(beginTime));
                if (exception) {
                    operationLogAddDTO.setResult(GlobalConst.FAIL);
                } else {
                    operationLogAddDTO.setResult(GlobalConst.SUCCESS);
                }
                operationLogAddDTO.setDescription(description);
                operationLogAddDTO.setDurationMs(System.currentTimeMillis() - beginTime);
                operationLogService.addOperationLog(operationLogAddDTO);
            } catch (Exception e) {
                logger.error("用户操作日志插入失败", e);
            }
        }
        return result;
    }

    private String handleTemplateDescParse(Object[] args, Method method, OperationLog operationLog) {
        // 获取被拦截方法参数名列表
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = discoverer.getParameterNames(method);

        // SPEL 上下文
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 将方法参数放入 SPEL 上下文；当参数名不可用时回退使用 argN
        if (paraNameArr == null || paraNameArr.length != args.length) {
            paraNameArr = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                // 回退参数名，避免 NPE
                paraNameArr[i] = "arg" + i;
            }
        }
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }

        String[] spelKeys = operationLog.spelKeys();
        Class[] beanClasses = operationLog.beanClasses();
        String[] findMethodNames = operationLog.findMethodNames();

        // 只检查 beanClasses 和 findMethodNames 的长度匹配
        if (beanClasses.length != findMethodNames.length) {
            logger.warn("beanClasses 和 findMethodNames 数量不匹配: beanClasses={}, findMethodNames={}",
                        beanClasses.length, findMethodNames.length);
            return "";
        }

        // 可选：检查 spelKeys 是否为空（如果 findMethodNames 需要参数的话）
        if (findMethodNames.length > 0 && spelKeys.length == 0) {
            // 检查是否有 findMethodNames 需要参数
            boolean needsParams = Arrays.stream(findMethodNames).anyMatch(m -> m.contains("#"));
            if (needsParams) {
                logger.warn("findMethodNames 需要参数但 spelKeys 为空");
                return "";
            }
        }

        StandardEvaluationContext userOperationLogContext = new StandardEvaluationContext();

        /** 第一步 参数解析（从方法参数解析出模板所需的变量）*/
        Map<String, Object> params = parseParams(spelKeys, context);
        setVariable(userOperationLogContext, params);

        /** 第二步参数转换成Object 业务对象*/
        parseBeans(userOperationLogContext, beanClasses);

        params = invokeFindMethods(findMethodNames, userOperationLogContext);
        setVariable(userOperationLogContext, params);

        /** 第三步 解析 模板*/
        return handleTemplateParse(operationLog.descriptionTemplate(), userOperationLogContext);
    }

    private String handleTemplateParse(String descriptionTemplate, StandardEvaluationContext userOperationLogContext) {
        // 复用解析器；模板本身的表达式解析出字符串描述
        return PARSER.parseExpression(descriptionTemplate).getValue(userOperationLogContext, String.class);
    }

    private void addModelInfo(OperationLogAddDTO operationLogAddDTO, Method method, Class clazz) {
        operationLogAddDTO.setModelCode("");
        operationLogAddDTO.setModelName("");

        // 获取方法上的 Permission 注解
        Permission methodPermission = AnnotatedElementUtils.findMergedAnnotation(method, Permission.class);
        // 获取类上的所有 Permission 注解（支持多个）
        Set<Permission> classPermissions = AnnotatedElementUtils.getMergedRepeatableAnnotations(clazz, Permission.class);

        if (Objects.nonNull(methodPermission) && StringUtils.isNotBlank(methodPermission.parentCode())) {
            // 遍历所有类上的 Permission 注解，找到与 modelCode 匹配的
            for (Permission classPermission : classPermissions) {
                if (Objects.nonNull(classPermission) &&
                        StringUtils.isNotBlank(classPermission.code()) &&
                        classPermission.code().equals(methodPermission.parentCode())) {
                    operationLogAddDTO.setModelCode(methodPermission.parentCode());
                    operationLogAddDTO.setModelName(classPermission.name());
                    break;
                }
            }
        }else{
            if (!classPermissions.isEmpty()){
                Permission classPermission = classPermissions.toArray(new Permission[0])[0];
                operationLogAddDTO.setModelCode(classPermission.code());
                operationLogAddDTO.setModelName(classPermission.name());
            }
        }
    }

    /**
     * 根据注解参数中的key 也就是spel表达式 从方法参数中获取得到 params的值
     * 规定格式为 a=b 则a的值为b的spel解析
     */
    private Map<String, Object> parseParams(String[] spelKeys, StandardEvaluationContext context) {
        Map<String, Object> name2Object = new HashMap<>();

        for (String key : spelKeys) {
            if (StringUtils.isNotBlank(key)) {
                // name=表达式 的形式；未指定 name 则将表达式路径中的 . 替换为 _
                String name = deduceParamName(key);
                String expression = key;

                String[] keys = splitKey(key);
                if (keys.length == 2) {
                    name = keys[0];
                    expression = keys[1];
                }
                // 使用复用的解析器求值
                name2Object.put(name, PARSER.parseExpression(SymbolConst.HASH + expression).getValue(context));
            }
        }
        return name2Object;
    }

    private String[] splitKey(String key) {
        return key.split(SymbolConst.EQUALS);
    }

    private String deduceParamName(String param) {
        return param.replace(SymbolConst.POINT, SymbolConst.UNDERLINE);
    }

    /**
     * 将参数放入计算上下文中
     */
    private void setVariable(StandardEvaluationContext userOperationLogContext, Map<String, Object> name2Object) {
        name2Object.forEach(userOperationLogContext::setVariable);
    }

    /**
     * 根据注解属性 解析 Bean
     * 通过类别获取，
     */
    private void parseBeans(StandardEvaluationContext userOperationLogContext, Class[] beanClasses) {
        for (int i = 0; i < beanClasses.length; i++) {
            Class beanClass = beanClasses[i];
            if (Objects.nonNull(beanClass)) {
                userOperationLogContext.setVariable(createBeanName(i), applicationContext.getBean(beanClass));
            }
        }
    }

    /**
     * 调用相关的方法，  格式 ： res=findById(#id)
     * 默认 结果名为 方法的名称 这种方式可能导致 参数的覆盖
     */
    private Map<String, Object> invokeFindMethods(String[] findMethodNames, StandardEvaluationContext userOperationLogContext) {
        Map<String, Object> name2Object = new HashMap<>();
        // 使用复用的 SpEL 解析器进行方法调用求值
        for (int i = 0; i < findMethodNames.length; i++) {

            String methodName = findMethodNames[i];
            if (StringUtils.isNotBlank(methodName)) {

                String name = deduceMethodName(methodName);
                String expression = methodName;
                String[] keys = splitKey(methodName);
                if (keys.length > 1) {
                    name = keys[0];
                    expression = keys[1];

                }
                name2Object.put(name, PARSER.parseExpression(SymbolConst.HASH + createBeanName(i) + SymbolConst.POINT + expression).getValue(userOperationLogContext));
            }
        }

        return name2Object;
    }

    /**
     * 推断方法的名称
     */
    private String deduceMethodName(String methodName) {
        if (methodName.contains(SymbolConst.BRACKET_LEFT)) {
            return methodName.substring(0, methodName.indexOf(SymbolConst.BRACKET_LEFT));
        }
        return methodName;
    }

    private String createBeanName(int i) {
        return BEAN + SymbolConst.UNDERLINE + i;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
