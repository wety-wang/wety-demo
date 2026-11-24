<script setup lang="ts">
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import { useLayoutMenuProvide } from '~/components/page-container/context'

const appStore = useAppStore()
const { theme } = storeToRefs(appStore)
const layoutMenu = useLayoutMenu()
useLayoutMenuProvide(layoutMenu, appStore)
</script>

<template>
  <!-- 固定 antd 语言为中文 -->
  <a-config-provider :theme="theme" :locale="zhCN">
    <a-app class="h-full font-chinese antialiased">
      <TokenProvider>
        <RouterView />
      </TokenProvider>
    </a-app>
  </a-config-provider>
</template>
