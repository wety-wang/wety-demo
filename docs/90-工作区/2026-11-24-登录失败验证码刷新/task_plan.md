# 任务计划：修复登录失败后验证码不刷新问题

## 目标
修复账号密码登录失败（验证码错误、账号或密码错误、账号被禁用等）后，前端图形验证码不刷新以及后端会话验证码未及时失效的问题。

## 实施步骤
1. 分析前后端登录与验证码校验流程，定位缺陷根因。
2. 修复前端 `frontend/src/pages/common/login.vue`：在登录请求失败后刷新图形验证码并清空已输入的验证码。
3. 修复后端 `backend/src/main/java/com/wety/demo/module/common/service/LoginService.java`：校验验证码时立即使其失效（单次有效）。
4. 补充后端单元测试 `LoginServiceTest.java`，覆盖验证码校验失败/成功后的会话清理行为。
5. 执行前端类型检查、生产构建与后端单元测试，验证无回归问题。
