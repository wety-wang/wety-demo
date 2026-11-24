# 进度记录

- [x] 定位前后端验证码刷新与失效缺陷
- [x] 修改 `frontend/src/pages/common/login.vue`：登录请求失败后刷新验证码并清空输入框
- [x] 修改 `backend/src/main/java/com/wety/demo/module/common/service/LoginService.java`：校验验证码后立即从 Session 移除
- [x] 补充后端单元测试 `LoginServiceTest.java`（2 个用例全部通过）
- [x] 执行前端类型检查（`vue-tsc --noEmit`）与生产构建（`vite build`），均通过
