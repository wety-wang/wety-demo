<script setup lang="ts">
import { CheckOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  theme?: 'light' | 'dark' | 'inverted' | 'top' | 'side' | 'mix'
  isDark?: boolean
  checked?: boolean
}>()
const prefixCls = shallowRef('ant-pro-drawer-setting-block-checkbox')
const cls = computed(() => {
  return {
    [`${prefixCls.value}-item`]: true,
    [`${prefixCls.value}-theme-item`]: props.isDark,
    [`${prefixCls.value}-item-${props.theme}`]: !!props.theme,
    [`${prefixCls.value}-theme-item-${props.theme}`]: props.isDark,
  }
})

// 中文常量映射：不同主题的提示文案
const PAGESTYLE_LABELS: Record<string, string> = {
  light: '亮色菜单风格',
  dark: '暗色菜单风格',
  inverted: '反转色菜单色风格',
  top: '顶部布局模式',
  side: '侧边栏布局模式',
  mix: '混合布局模式',
}

const { token } = useAntdToken()
</script>

<template>
  <a-tooltip>
    <template #title>
      <!-- 使用中文常量映射，移除对 t() 的依赖 -->
      {{ theme ? PAGESTYLE_LABELS[theme] : '' }}
    </template>
    <div :class="cls">
      <CheckOutlined v-show="checked" :style="{ color: token?.colorPrimary }" :class="`${prefixCls}-selectIcon`" />
    </div>
  </a-tooltip>
</template>
