# 调研发现

1. **前端缺陷**：`frontend/src/pages/common/login.vue` 的 `submit()` 方法在 `catch (e)` 中仅更新 `submitLoading.value = false`（以及仅对 `AxiosError` 设置 `errorAlert`），未调用 `refreshCaptcha()`，也未清空 `loginModel.checkCode`。
2. **错误传播机制**：`frontend/src/utils/request.ts` 在业务码非 `200` 时通过 `Promise.reject(new Error(businessMsg))` 抛出错误，HTTP 错误时抛出 `error.response`，均会进入 `submit()` 的 `catch` 分支。
3. **后端隐患**：`LoginService.checkImage()` 校验 `HttpSession` 中的 `GlobalConst.IMAGE_CODE` 后未移除该属性，导致同一验证码在校验失败或账号密码错误后仍可被重复提交。
