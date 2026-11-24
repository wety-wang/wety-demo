<script setup lang="ts">
import type { LoginMobileParams, LoginParams } from '~@/api/common/login'
import { AlipayCircleFilled, LockOutlined, MobileOutlined, TaobaoCircleFilled, UserOutlined, WeiboCircleFilled } from '@ant-design/icons-vue'
import { delayTimer } from '@v-c/utils'
import { AxiosError } from 'axios'
import pageBubble from '@/utils/page-bubble'
import { loginApi } from '~/api/common/login'
import { useAccountName } from '~/composables/account'
import GlobalLayoutFooter from '~/layouts/components/global-footer/index.vue'
import { getQueryParam } from '~/utils/tools'

const message = useMessage()
const notification = useNotification()
const appStore = useAppStore()
const { layoutSetting } = storeToRefs(appStore)
const router = useRouter()
const token = useAuthorization()
const loginModel = reactive({
  loginName: 'admin' as unknown as string,
  password: '123456' as unknown as string,
  checkCode: undefined as unknown as string,
  mobile: undefined as unknown as string,
  code: undefined as unknown as string,
  type: 'account',
  remember: true,
})
const formRef = shallowRef()
const codeLoading = shallowRef(false)
const resetCounter = 60
const submitLoading = shallowRef(false)
const errorAlert = shallowRef(false)
const bubbleCanvas = ref<HTMLCanvasElement>()
// 图形验证码刷新用的时间戳（避免缓存）
const captchaTs = ref(Date.now())
const captchaBase = import.meta.env.VITE_APP_BASE_API
// 计算验证码图片地址（携带时间戳以避免浏览器缓存）
const captchaUrl = computed(() => `${captchaBase}/bac/common/getVerifyImage?ts=${captchaTs.value}`)
// 刷新验证码：更新时间戳即可重新拉取图片
function refreshCaptcha() {
  captchaTs.value = Date.now()
}
const { counter, pause, reset, resume, isActive } = useInterval(1000, {
  controls: true,
  immediate: false,
  callback(count) {
    if (count) {
      if (count === resetCounter)
        pause()
    }
  },
})
async function getCode() {
  codeLoading.value = true
  await formRef.value.validate(['mobile'])
  setTimeout(() => {
    reset()
    resume()
    codeLoading.value = false
    message.success('验证码是：123456')
  }, 3000)
}

async function submit() {
  submitLoading.value = true
  try {
    await formRef.value?.validate()
    let params: LoginParams | LoginMobileParams

    if (loginModel.type === 'account') {
      params = {
        loginName: loginModel.loginName,
        password: loginModel.password,
        checkCode: loginModel.checkCode,
        type: 'account',
      }
    }
    else {
      params = {
        mobile: loginModel.mobile,
        code: loginModel.code,
        type: 'mobile',
      } as unknown as LoginMobileParams
    }
    const data = await loginApi(params)
    // 登录成功：写入 token 与账户名（供签名使用）
    token.value = data?.token
    const accountName = useAccountName()
    accountName.value = data?.loginName ?? loginModel.loginName
    notification.success({
      message: '登录成功',
      description: '欢迎回来！',
      duration: 3,
    })
    // 获取当前是否存在重定向的链接，如果存在就走重定向的地址
    const redirect = getQueryParam('redirect', '/')
    await router.push({
      path: redirect,
      replace: true,
    })
  }
  catch (e) {
    if (e instanceof AxiosError)
      errorAlert.value = true

    submitLoading.value = false
  }
}
onMounted(async () => {
  await delayTimer(300)
  pageBubble.init(unref(bubbleCanvas)!)
})

onBeforeUnmount(() => {
  pageBubble.removeListeners()
})
</script>

<template>
  <div class="login-container">
    <div h-screen w-screen absolute z-10>
      <canvas ref="bubbleCanvas" />
    </div>
    <div class="login-content flex-center">
      <div class="ant-pro-form-login-main rounded">
        <!-- 登录头部 -->
        <div
          class="flex-between h-15 px-4 mb-[2px]"
        >
          <div class="flex-end">
            <span class="ant-pro-form-login-logo">
              <img w-full h-full object-cover src="/logo.svg">
            </span>
            <span class="ant-pro-form-login-title">
              Antdv Pro
            </span>
            <span class="ant-pro-form-login-desc">
              <!-- 登录页副标题中文常量 -->
              Antdv Pro - 高质量中后台模板
            </span>
          </div>
          <div class="login-lang flex-center relative z-11">
            <span
              class="flex-center cursor-pointer text-16px"
              @click="appStore.toggleTheme(layoutSetting.theme === 'dark' ? 'light' : 'dark')"
            >
              <!-- 亮色和暗黑模式切换按钮 -->
              <template v-if="layoutSetting.theme === 'light'">
                <carbon-moon />
              </template>
              <template v-else>
                <carbon-sun />
              </template>
            </span>
            <!-- <SelectLang /> -->
          </div>
        </div>
        <a-divider m-0 />
        <!-- 登录主体 -->
        <div class="box-border flex min-h-[520px]">
          <!-- 登录框左侧 -->
          <div class="ant-pro-form-login-main-left min-h-[520px] flex-center  bg-[var(--bg-color-container)]">
            <img src="@/assets/images/login-left.png" class="h-5/6 w-5/6">
          </div>
          <a-divider m-0 type="vertical" class="ant-pro-login-divider  min-h-[520px]" />
          <!-- 登录框右侧 -->
          <div class="ant-pro-form-login-main-right px-5 w-[335px] flex-center flex-col relative z-11">
            <div class="text-center py-6 text-2xl">
              <!-- 登录提示语 -->
              欢迎登录，请输入账户信息
            </div>
            <a-form ref="formRef" :model="loginModel">
              <a-tabs v-model:active-key="loginModel.type" centered>
                <!-- 标签页：账户密码登录 / 手机号登录 -->
                <a-tab-pane key="account" tab="账户密码登录" />
                <a-tab-pane key="mobile" tab="手机号登录" />
              </a-tabs>
              <!-- 判断是否存在error -->
              <a-alert
                v-if="errorAlert && loginModel.type === 'account'" mb-24px
                message="账户或密码错误" type="error" show-icon
              />
              <a-alert
                v-if="errorAlert && loginModel.type === 'mobile'" mb-24px
                message="验证码或手机号错误" type="error" show-icon
              />
              <template v-if="loginModel.type === 'account'">
                <a-form-item name="loginName" :rules="[{ required: true, message: '请输入用户名！' }]">
                  <a-input
                    v-model:value="loginModel.loginName" allow-clear
                    autocomplete="off"
                    placeholder="用户名" size="large" @press-enter="submit"
                  >
                    <template #prefix>
                      <UserOutlined />
                    </template>
                  </a-input>
                </a-form-item>
                <a-form-item name="password" :rules="[{ required: true, message: '请输入密码！' }]">
                  <a-input-password
                    v-model:value="loginModel.password" allow-clear
                    placeholder="密码" size="large" @press-enter="submit"
                  >
                    <template #prefix>
                      <LockOutlined />
                    </template>
                  </a-input-password>
                </a-form-item>
                <!-- 图形验证码：后端流式输出图片，点击可刷新 -->
                <a-form-item name="checkCode" :rules="[{ required: true, message: '请输入验证码！' }]">
                  <div class="flex items-center">
                    <a-input
                      v-model:value="loginModel.checkCode"
                      allow-clear
                      placeholder="图形验证码"
                      size="large"
                      style="flex: 1 1 0%; margin-right: 8px;"
                      @press-enter="submit"
                    >
                      <template #prefix>
                        <LockOutlined />
                      </template>
                    </a-input>
                    <img
                      :src="captchaUrl"
                      alt="captcha"
                      style="height: 40px; cursor: pointer; border-radius: 4px;"
                      @click="refreshCaptcha"
                    >
                  </div>
                </a-form-item>
              </template>
              <template v-if="loginModel.type === 'mobile'">
                <a-form-item
                  name="mobile" :rules="[
                    { required: true, message: '请输入手机号！' },
                    {
                      pattern: /^(86)?1([38][0-9]|4[579]|5[0-35-9]|6[6]|7[0135678]|9[89])[0-9]{8}$/,
                      message: '手机号格式错误！',
                    },
                  ]"
                >
                  <a-input
                    v-model:value="loginModel.mobile" allow-clear
                    placeholder="手机号" size="large" @press-enter="submit"
                  >
                    <template #prefix>
                      <MobileOutlined />
                    </template>
                  </a-input>
                </a-form-item>
                <a-form-item name="code" :rules="[{ required: true, message: '请输入验证码！' }]">
                  <div flex items-center>
                    <a-input
                      v-model:value="loginModel.code"
                      style="flex: 1 1 0%; transition: width 0.3s ease 0s; margin-right: 8px;" allow-clear
                      placeholder="验证码" size="large" @press-enter="submit"
                    >
                      <template #prefix>
                        <LockOutlined />
                      </template>
                    </a-input>
                    <a-button :loading="codeLoading" :disabled="isActive" size="large" @click="getCode">
                      <template v-if="!isActive">
                        获取验证码
                      </template>
                      <template v-else>
                        {{ resetCounter - counter }} 秒
                      </template>
                    </a-button>
                  </div>
                </a-form-item>
              </template>
              <div class="mb-24px flex-between">
                <a-checkbox v-model:checked="loginModel.remember">
                  自动登录
                </a-checkbox>
                <a>忘记密码</a>
              </div>
              <a-button type="primary" block :loading="submitLoading" size="large" @click="submit">
                登录
              </a-button>
            </a-form>
            <a-divider>
              <span class="text-slate-500">其他登录方式</span>
            </a-divider>
            <div class="ant-pro-form-login-other">
              <AlipayCircleFilled class="icon" />
              <TaobaoCircleFilled class="icon" />
              <WeiboCircleFilled class="icon" />
            </div>
          </div>
        </div>
      </div>
    </div>
    <div py-24px px-50px fixed bottom-0 z-11 w-screen :data-theme="layoutSetting.theme" text-14px>
      <GlobalLayoutFooter
        :copyright="layoutSetting.copyright" icp="鲁ICP备2023021414号-2"
      >
        <template #renderFooterLinks>
          <footer-links />
        </template>
      </GlobalLayoutFooter>
    </div>
  </div>
</template>

<style lang="less" scoped>
.login-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: auto;
  background: var(--bg-color-container);
}

.login-lang {
  height: 40px;
  line-height: 44px;
}

.login-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.ant-pro-form-login-container {
  display: flex;
  flex: 1 1;
  flex-direction: column;
  height: 100%;
  padding: 32px 0;
  overflow: auto;
  background: inherit;
}

.ant-pro-form-login-header a {
  text-decoration: none;
}

.ant-pro-form-login-title {
  color: var(--text-color);
  font-weight: 600;
  font-size: 33px;
  line-height: 1;
}

.ant-pro-form-login-logo {
  width: 44px;
  height: 44px;
  margin-right: 16px;
  vertical-align: top;
}

.ant-pro-form-login-desc {
  color: var(--text-color-1);
  font-size: 14px;
  margin-left: 16px;
}

.ant-pro-form-login-main-right {
  .ant-tabs-nav-list {
    margin: 0 auto;
    font-size: 16px;
  }

  .ant-pro-form-login-other {
    line-height: 22px;
    text-align: center;
  }
}

.ant-pro-form-login-main {
  box-shadow: var(--c-shadow);
}

.icon {
  margin-left: 8px;
  color: var(--text-color-2);
  font-size: 24px;
  vertical-align: middle;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: var(--pro-ant-color-primary);
  }
}
.login-media(@width:100%) {
  .ant-pro-form-login-main {
    width: @width;
  }
  .ant-pro-form-login-main-left {
    display: none;
  }
  .ant-pro-form-login-main-right {
    width: 100%;
  }
  .ant-pro-form-login-desc {
    display: none;
  }
}
@media (min-width: 992px) {
  .ant-pro-form-login-main-left {
    width: 700px;
  }
}
@media (min-width: 768px) and (max-width: 991px) {
  .ant-pro-login-divider {
    display: none;
  }
  .login-media(400px);
}
@media screen and (max-width: 767px) {
  .login-media(350px);

  .ant-pro-login-divider {
    display: none;
  }
}
</style>
