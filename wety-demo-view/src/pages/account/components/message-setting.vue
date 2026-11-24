<script setup lang="ts">
interface DataItem {
  title: string
  desc: string
  checked: boolean
}
const data = reactive<DataItem[]>([
  { title: '系统消息', desc: '系统重要信息通知', checked: true },
  { title: '用户消息', desc: '用户动态提醒', checked: true },
  { title: '活动消息', desc: '活动与推广通知', checked: true },
])
</script>

<template>
  <!-- 标题：消息通知 -->
  <a-card title="消息通知" :bordered="false">
    <a-list item-layout="horizontal" :data-source="data">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta
            :description="item.desc"
          >
            <template #title>
              <a href="https://www.antdv.com/">{{ item.title }}</a>
            </template>
          </a-list-item-meta>
          <template #actions>
            <a-switch v-model:checked="item.checked" />
          </template>
        </a-list-item>
      </template>
    </a-list>
  </a-card>
</template>
