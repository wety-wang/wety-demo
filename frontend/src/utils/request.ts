import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import md5 from 'js-md5'
import { ContentTypeEnum, RequestEnum } from '~#/http-enum'
import { useAccountName } from '~/composables/account'
import { STORAGE_AUTHORIZE_KEY, useAuthorization } from '~/composables/authorization'
import router from '~/router'
import { AxiosLoading } from './loading'

export interface ResponseBody<T = any> {
  code: string
  data?: T
  message?: string
  duration?: number
}

export interface RequestConfigExtra {
  token?: boolean
  loading?: boolean
}
const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API ?? '/',
  timeout: 60000,
  headers: { 'Content-Type': ContentTypeEnum.JSON },
})
const axiosLoading = new AxiosLoading()
async function requestHandler(config: InternalAxiosRequestConfig & RequestConfigExtra): Promise<InternalAxiosRequestConfig> {
  // 统一使用 /api 作为前缀，开发环境通过 Vite 代理到后端
  const token = useAuthorization()
  const accountName = useAccountName()

  if (token.value && config.token !== false)
    config.headers.set(STORAGE_AUTHORIZE_KEY, token.value)

  // 将 Accept-Language 固定为中文，避免浏览器或应用内切换影响后端行为
  config.headers.set('Accept-Language', 'zh-CN')

  // 为后端安全校验添加签名参数（ACCOUNTNAME、TIMESTAMP、SIGN）
  // - 排除免签路径（登录与验证码图片）
  // - 将参数统一追加到 query（即 config.params），避免 POST JSON 不能被后端 getParameter 解析
  try {
    const urlPath = config.url || ''
    // 免签路径：登录与验证码图片（其余 /bac/* 接口统一加签）
    const NO_SIGN_PATHS = ['/bac/common/login', '/bac/common/getVerifyImage']
    // 覆盖所有以 /bac 开头的接口（与后端鉴权路径一致），排除免签路径
    const needSign = urlPath.startsWith('/bac') && !NO_SIGN_PATHS.includes(urlPath) && token.value && accountName.value

    if (needSign) {
      const timestamp = Date.now().toString()
      const strToSign = `${urlPath}?ACCOUNTNAME=${accountName.value}&TIMESTAMP=${timestamp}&TOKEN=${token.value}`
      const sign = md5(strToSign)
      // 注入到查询参数中，确保后端能够通过 getParameter 读取
      const params = new URLSearchParams(config.params)
      params.set('ACCOUNTNAME', accountName.value as string)
      params.set('TIMESTAMP', timestamp)
      params.set('SIGN', sign)
      config.params = params
    }
  }
  catch (e) {
    // 安全兜底：签名失败不影响非受控接口，但建议记录日志或上报
    console.error('签名失败:', e)
  }
  if (config.loading)
    axiosLoading.addLoading()
  return config
}

function responseHandler(response: AxiosResponse): any {
  const res = response.data as ResponseBody

  if (res && typeof res === 'object' && 'code' in res) {
    const businessCode = String((res as any).code)
    if (businessCode !== '200') {
      const notification = useNotification()
      const businessMsg = (res as any).message || (res as any).msg || '服务错误'
      // 统一错误提示
      notification?.error({
        message: businessCode,
        description: businessMsg,
        duration: 3,
      })
      // 401 需要清空登录状态并跳转登录
      if (businessCode === '401') {
        console.log('401 触发登出逻辑')
        const token = useAuthorization()
        token.value = null
        const { fullPath, query: { redirect } } = router.currentRoute.value
        router.push({
          path: '/login',
          query: {
            redirect: redirect || fullPath,
          },
        }).then(() => {})
      }
      // 中断后续流程，抛出业务错误
      return Promise.reject(new Error(businessMsg))
    }
    // 成功：直接返回统一结构（调用方通常取 res.data）
    return res.data
  }
  // 兜底：非统一结构（如图片/文件流），直接原样返回
  return res
}

function errorHandler(error: AxiosError): Promise<any> {
  const token = useAuthorization()
  const notification = useNotification()

  if (error.response) {
    const { data, status, statusText } = error.response as AxiosResponse<ResponseBody>
    if (status === 401) {
      notification?.error({
        message: '401',
        // 后端字段为 message，这里兼容 msg 与 message
        description: data?.message || (data as any)?.msg || statusText,
        duration: 3,
      })
      /**
       * 这里处理清空用户信息和token的逻辑，后续扩展
       */
      token.value = null

      const { fullPath, query: { redirect } } = router.currentRoute.value
      router
        .push({
          path: '/login',
          query: {
            redirect: redirect || fullPath,
          },
        })
        .then(() => {})
    }
    else if (status === 403) {
      notification?.error({
        message: '403',
        description: data?.message || (data as any)?.msg || statusText,
        duration: 3,
      })
    }
    else if (status === 500) {
      notification?.error({
        message: '500',
        description: data?.message || (data as any)?.msg || statusText,
        duration: 3,
      })
    }
    else {
      notification?.error({
        message: '服务错误',
        description: data?.message || (data as any)?.msg || statusText,
        duration: 3,
      })
    }
  }
  return Promise.reject(error.response)
}

interface AxiosOptions<T> {
  url: string
  params?: T
  data?: T
}
instance.interceptors.request.use(requestHandler)

instance.interceptors.response.use(responseHandler, errorHandler)
function instancePromise<R = any, T = any>(options: AxiosOptions<T> & RequestConfigExtra): Promise<R> {
  const { loading } = options
  return new Promise((resolve, reject) => {
    instance.request(options).then((res) => {
      resolve(res as any)
    }).catch((e: Error | AxiosError) => {
      reject(e)
    }).finally(() => {
      if (loading) {
        axiosLoading.closeLoading()
      }
    })
  })
}
export function useGet<R = any, T = any>(url: string, params?: T, config?: AxiosRequestConfig & RequestConfigExtra): Promise<R> {
  const options = {
    url,
    params,
    method: RequestEnum.GET,
    ...config,
  }
  return instancePromise<R, T>(options)
}

export function usePost<R = any, T = any>(url: string, data?: T, config?: AxiosRequestConfig & RequestConfigExtra): Promise<R> {
  const options = {
    url,
    data,
    method: RequestEnum.POST,
    ...config,
  }
  return instancePromise<R, T>(options)
}

export function usePut<R = any, T = any>(url: string, data?: T, config?: AxiosRequestConfig & RequestConfigExtra): Promise<R> {
  const options = {
    url,
    data,
    method: RequestEnum.PUT,
    ...config,
  }
  return instancePromise<R, T>(options)
}

export function useDelete<R = any, T = any>(url: string, data?: T, config?: AxiosRequestConfig & RequestConfigExtra): Promise<R> {
  const options = {
    url,
    data,
    method: RequestEnum.DELETE,
    ...config,
  }
  return instancePromise<R, T>(options)
}
