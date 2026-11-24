<script setup lang="ts">
defineOptions({
  name: 'BasicProfile',
})
</script>

<template>
  <page-container>
    <a-card :bordered="false">
      <!-- 标题：订单详情 -->
      <a-descriptions title="订单详情">
        <!-- 标签：订单号 -->
        <a-descriptions-item label="订单号">
          1000000000
        </a-descriptions-item>
        <!-- 标签：状态 -->
        <a-descriptions-item label="状态">
          已完成
        </a-descriptions-item>
        <!-- 标签：交易编号 -->
        <a-descriptions-item label="交易编号">
          1234123421
        </a-descriptions-item>
        <!-- 标签：子订单号 -->
        <a-descriptions-item label="子订单号">
          3214321432
        </a-descriptions-item>
      </a-descriptions>
      <a-divider style="margin-bottom: 32px" />
      <!-- 标题：客户信息 -->
      <a-descriptions title="客户信息">
        <!-- 标签：客户姓名 -->
        <a-descriptions-item label="客户姓名">
          张三
        </a-descriptions-item>
        <!-- 标签：联系电话 -->
        <a-descriptions-item label="联系电话">
          18812345678
        </a-descriptions-item>
        <!-- 标签：配送方式 -->
        <a-descriptions-item label="配送方式">
          快速配送
        </a-descriptions-item>
        <!-- 标签：送货地址 -->
        <a-descriptions-item label="送货地址">
          北京市朝阳区建国路123号
        </a-descriptions-item>
        <!-- 标签：备注 -->
        <a-descriptions-item label="备注">
          无特殊要求
        </a-descriptions-item>
      </a-descriptions>
      <a-divider style="margin-bottom: 32px" />
      <!-- 标题：产品信息 -->
      <a-descriptions title="产品信息">
        <!-- 标签：产品名称 -->
        <a-descriptions-item label="产品名称">
          商品A
        </a-descriptions-item>
        <!-- 标签：单价 -->
        <a-descriptions-item label="单价">
          $50.00
        </a-descriptions-item>
        <!-- 标签：数量 -->
        <a-descriptions-item label="数量">
          3
        </a-descriptions-item>
        <!-- 标签：小计 -->
        <a-descriptions-item label="小计">
          $150.00
        </a-descriptions-item>
      </a-descriptions>
      <a-divider style="margin-bottom: 32px" />
      <!-- 标题：支付信息 -->
      <a-descriptions title="支付信息">
        <!-- 标签：支付方式 -->
        <a-descriptions-item label="支付方式">
          在线支付
        </a-descriptions-item>
        <!-- 标签：支付时间 -->
        <a-descriptions-item label="支付时间">
          2023-08-18 14:30:00
        </a-descriptions-item>
        <!-- 标签：支付金额 -->
        <a-descriptions-item label="支付金额">
          $150.00
        </a-descriptions-item>
        <!-- 标签：支付状态 -->
        <a-descriptions-item label="支付状态">
          已支付
        </a-descriptions-item>
      </a-descriptions>
    </a-card>
  </page-container>
</template>

<style lang="less" scoped>
.title {
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
}
</style>
