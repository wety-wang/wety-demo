<script setup lang="ts">
import { AlipayOutlined, DingdingOutlined, TaobaoOutlined } from '@ant-design/icons-vue'

interface DataItem {
  title: string
  avatar: string
}
const data = computed<DataItem[]>(() => {
  return [
    { title: '淘宝账号', avatar: 'TaobaoOutlined' },
    { title: '支付宝账号', avatar: 'AlipayOutlined' },
    { title: '钉钉账号', avatar: 'DingdingOutlined' },
  ]
})
</script>

<template>
  <!-- 标题：账号绑定 -->
  <a-card title="账号绑定" :bordered="false">
    <a-list item-layout="horizontal" :data-source="data">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta
            description="未绑定"
          >
            <template #title>
              <a href="https://www.antdv.com/">{{ item.title }}</a>
            </template>
            <template #avatar>
              <TaobaoOutlined v-if="item.avatar === 'TaobaoOutlined' " style="color: #ff4000;" class="account-setting-avatar" />
              <AlipayOutlined v-if="item.avatar === 'AlipayOutlined' " style="color: #2eabff" class="account-setting-avatar" />
              <DingdingOutlined v-if="item.avatar === 'DingdingOutlined' " style="color: #fff; background-color: #2eabff" class="account-setting-avatar" />
            </template>
          </a-list-item-meta>
          <template #actions>
            <a-button type="link">
              绑定
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
.account-setting-avatar {
  font-size: 48px;
  line-height: 48px;
  border-radius: 2px;
}
</style>
