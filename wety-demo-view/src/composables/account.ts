// 全局存储账户名（ACCOUNTNAME），供请求签名拦截器使用
export const STORAGE_ACCOUNTNAME_KEY = 'ACCOUNTNAME'

export const useAccountName = createGlobalState(() =>
  useStorage<null | string>(STORAGE_ACCOUNTNAME_KEY, null),
)
