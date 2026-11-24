<script setup lang="ts">
const locale = ref('zh-CN')
function handleClick(_evt: any) {
  // 目前系统仅支持中文，点击操作不做实际语言切换
  locale.value = 'zh-CN'
}
</script>

<template>
  <a-dropdown>
    <span hover="bg-[var(--hover-color)]" flex items-center h-48px px-12px text-16px cursor-pointer class="transition-all-300">
      <CarbonLanguage class="anticon" />
    </span>
    <template #overlay>
      <a-menu :selected-keys="[locale]" @click="handleClick">
        <a-menu-item key="zh-CN">
          <template #icon>
            <span>
              🇨🇳
            </span>
          </template>
          简体中文
        </a-menu-item>
        <a-menu-item key="en-US">
          <template #icon>
            <span>
              🇺🇸
            </span>
          </template>
          English
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>
