import { encryptSM2 } from '~/utils/crypto'

// 登录参数（账号登录）
export interface LoginParams {
  loginName: string
  password: string
  checkCode: string
  type?: 'account'
}

// 登录参数（手机号登录）
export interface LoginMobileParams {
  mobile: string
  code: string
  type: 'mobile'
}

// 登录返回
export interface LoginResultModel {
  accountCode: string
  loginName: string
  userName: string
  token: string
}

export function loginApi(params: LoginParams | LoginMobileParams) {
  // 这里通过“是否存在 mobile 字段”来区分登录方式，避免联合类型比较报错
  const isMobile = 'mobile' in (params as any)
  const payload: any = isMobile
    ? params
    : {
        loginName: (params as LoginParams).loginName,
        password: encryptSM2((params as LoginParams).password),
        checkCode: (params as LoginParams).checkCode,
      }
  return usePost<LoginResultModel, typeof payload>('/bac/common/login', payload, {
    token: false,
    loading: true,
  })
}

export function logoutApi() {
  return useGet('/bac/common/logout', undefined, {
    loading: true,
  })
}
