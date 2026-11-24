<script setup lang="ts">
import dayjs from 'dayjs'

const activeKey = ref()

const columns = ref([
  {
    // 列标题：操作类型
    title: '操作类型',
    dataIndex: 'type',
    key: 'type',
    slots: { customRender: 'type' },
  },
  {
    // 列标题：操作人
    title: '操作人',
    dataIndex: 'owner',
    key: 'owner',
    width: 100,
  },
  {
    // 列标题：结果
    title: '结果',
    dataIndex: 'result',
    key: 'result',
  },
  {
    // 列标题：开始时间
    title: '开始时间',
    dataIndex: 'time',
    key: 'time',
  },
  {
    // 列标题：备注
    title: '备注',
    dataIndex: 'info',
    key: 'info',
  },
])

const data = [
  {
    key: '1',
    type: '创建订单',
    owner: '烟雨',
    result: '成功',
    time: dayjs().format('YYYY-MM-DD hh:mm'),
    info: '无',
  },
  {
    key: '2',
    type: '创建订单',
    owner: '烟雨',
    result: '成功',
    time: dayjs().format('YYYY-MM-DD hh:mm'),
    info: '无',
  },
  {
    key: '3',
    type: '创建订单',
    owner: '烟雨',
    result: '成功',
    time: dayjs().format('YYYY-MM-DD hh:mm'),
    info: '无',
  },
]

function getI18n(key: any) {
  // 表头标题中文直写，保留自定义渲染逻辑
  if (key === 'type')
    return '操作类型'
  else if (key === 'owner')
    return '操作人'
  else if (key === 'result')
    return '结果'
  else if (key === 'time')
    return '开始时间'
  else if (key === 'info')
    return '备注'
}
</script>

<template>
  <a-card class="my-6">
    <a-tabs v-model:active-key="activeKey">
      <a-tab-pane key="1" tab="日志一">
        <a-table :data-source="data" :columns="columns">
          <template #headerCell="scope">
            {{ getI18n(scope?.column?.key) }}
          </template>
        </a-table>
      </a-tab-pane>
      <a-tab-pane key="2" tab="日志二">
        <a-table :data-source="data" :columns="columns" />
      </a-tab-pane>
      <a-tab-pane key="3" tab="日志三">
        <a-table :data-source="data" :columns="columns" />
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>
