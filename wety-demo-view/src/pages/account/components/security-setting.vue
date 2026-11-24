<script setup lang="ts">
interface DataItem {
  title: string
  desc: string
}
const data = computed<DataItem[]>(() => {
  return [
    { title: '账户密码', desc: '当前密码强度：强' },
    { title: '手机绑定', desc: '已绑定手机：168****6666' },
    { title: '密保问题', desc: '未设置密保问题' },
    { title: '备用邮箱', desc: '已绑定邮箱：AntdvPro@abc.com' },
    { title: 'MFA设备', desc: '未开启多因素认证' },
  ]
})
</script>

<template>
  <!-- 标题：安全设置 -->
  <a-card title="安全设置" :bordered="false">
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
            <a-button type="link">
              修改
            </a-button>
          </template>
        </a-list-item>
      </template>
    </a-list>
  </a-card>
</template>

<style scoped lang="less">
:deep(.ant-card-body) {
  padding-left: 0 !important;
}
</style>
