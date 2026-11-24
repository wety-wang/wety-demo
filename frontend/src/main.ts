import type { App } from 'vue'
import dayjs from 'dayjs'
import { createPinia } from 'pinia'
import { createApp } from 'vue'
import router from '~/router'
import Root from './App.vue'
import {
  setupAccessDirective,
  setupLoadingDirective,
} from './directive'
import 'dayjs/locale/zh-cn'
import '~/router/router-guard'
import 'ant-design-vue/dist/reset.css'
import '~/assets/styles/reset.css'
import 'uno.css'

const pinia = createPinia()
async function start() {
  const app: App = createApp(Root)
  app.use(pinia)
  dayjs.locale('zh-cn')
  setupDirective(app)
  app.use(router)
  app.mount('#app')
  app.config.performance = true
}

function setupDirective(app: App) {
  // 注册loading自定义指令
  setupLoadingDirective(app)
  setupAccessDirective(app)
}
start()
