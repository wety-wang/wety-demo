<script setup lang="ts">
import type { CheckedType } from '~@/layouts/basic-layout/typing'

const props = defineProps<{
  colorWeak?: boolean
  colorGray?: boolean
}>()
const emit = defineEmits(['changeSetting'])
const list = computed(() => ([
  {
    // 固定中文常量：色弱模式
    title: '色弱模式',
    key: 'colorWeak',
    disabled: false,
    disabledReason: '',
  },
  {
    // 固定中文常量：灰色模式
    title: '灰色模式',
    key: 'colorGray',
    disabled: false,
    disabledReason: '',
  },
]))
function handleToggleChange(key: string, value: CheckedType) {
  emit('changeSetting', key, value)
}
function isToggleChecked(key: string) {
  return Reflect.get(props, key)
}
</script>

<template>
  <a-list :data-source="list" :split="false">
    <template #renderItem="{ item }">
      <a-list-item>
        <template #actions>
          <a-switch
            size="small"
            :checked="isToggleChecked(item.key)"
            :disabled="item.disabled"
            @update:checked="handleToggleChange(item.key, $event)"
          />
        </template>
        <span :style="{ opacity: item.disabled ? '0.5' : '1' }">
          <!-- 使用中文常量 item.title，移除 t() 依赖 -->
          {{ item.title }}
        </span>
      </a-list-item>
    </template>
  </a-list>
</template>
