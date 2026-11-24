<script setup lang="ts">
import { CheckOutlined } from '@ant-design/icons-vue'

defineProps<{
  colorList: ({ key: string, color: string })[]
  color?: string
  onChange?: (color: string) => void
}>()

const prefixCls = shallowRef('ant-pro-drawer-setting-theme-color')

// 中文常量映射：主题色名称
const THEME_COLOR_LABELS: Record<string, string> = {
  dust: '薄暮',
  volcano: '火山',
  sunset: '日暮',
  cyan: '明青',
  green: '极光绿',
  daybreak: '拂晓',
  techBlue: '科技（默认）',
  geekblue: '极客蓝',
  purple: '酱紫',
}
</script>

<template>
  <div :class="`${prefixCls}`">
    <div :class="`${prefixCls}-content`">
      <a-tooltip
        v-for="item in colorList"
        :key="item.color"
      >
        <template #title>
          <!-- 使用中文常量映射，移除对 t() 的依赖 -->
          {{ THEME_COLOR_LABELS[item.key] ?? '' }}
        </template>
        <div
          :class="`${prefixCls}-block`"
          :style="{ backgroundColor: item.color }"
          @click="onChange?.(item.color)"
        >
          <CheckOutlined v-show="color === item.color" />
        </div>
      </a-tooltip>
    </div>
  </div>
</template>
