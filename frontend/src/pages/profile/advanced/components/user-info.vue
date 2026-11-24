<script setup lang="ts">
import dayjs from 'dayjs'
</script>

<template>
  <a-card :bordered="false" class="my-6">
    <!-- 标题：客户信息 -->
    <a-descriptions title="客户信息">
      <!-- 标签：客户姓名 -->
      <a-descriptions-item label="客户姓名">
        张三
      </a-descriptions-item>
      <!-- 标签：客户卡号 -->
      <a-descriptions-item label="客户卡号">
        xxx
      </a-descriptions-item>
      <!-- 标签：身份证 -->
      <a-descriptions-item label="身份证">
        xxx
      </a-descriptions-item>
      <!-- 标签：联系电话 -->
      <a-descriptions-item label="联系电话">
        16866666666
      </a-descriptions-item>
      <!-- 标签：送货地址 -->
      <a-descriptions-item label="送货地址">
        张三 16866666666 广东省广州市
      </a-descriptions-item>
    </a-descriptions>
    <a-divider style="margin-bottom: 32px" />
    <!-- 信息组 -->
    <a-descriptions title="信息组">
      <a-descriptions-item label="数据ID">
        688
      </a-descriptions-item>
      <a-descriptions-item label="本数据更新时间">
        {{ dayjs().format('YYYY-MM-DD hh:mm') }}
      </a-descriptions-item>
      <a-descriptions-item label="数据ID">
        688
      </a-descriptions-item>
      <a-descriptions-item label="本数据更新时间">
        {{ dayjs().format('YYYY-MM-DD hh:mm') }}
      </a-descriptions-item>
      <a-descriptions-item label="数据ID">
        688
      </a-descriptions-item>
    </a-descriptions>
    <a-card title="信息组">
      <a-descriptions>
        <a-descriptions-item label="数据ID">
          688
        </a-descriptions-item>
        <a-descriptions-item label="本数据更新时间">
          {{ dayjs().format('YYYY-MM-DD hh:mm') }}
        </a-descriptions-item>
        <a-descriptions-item label="数据ID">
          688
        </a-descriptions-item>
        <a-descriptions-item label="本数据更新时间">
          {{ dayjs().format('YYYY-MM-DD hh:mm') }}
        </a-descriptions-item>
        <a-descriptions-item label="数据ID">
          688
        </a-descriptions-item>
      </a-descriptions>
      <a-divider style="margin-bottom: 32px" />
      <a-descriptions>
        <a-descriptions-item label="数据ID">
          688
        </a-descriptions-item>
        <a-descriptions-item label="本数据更新时间">
          {{ dayjs().format('YYYY-MM-DD hh:mm') }}
        </a-descriptions-item>
        <a-descriptions-item label="数据ID">
          688
        </a-descriptions-item>
        <a-descriptions-item label="本数据更新时间">
          {{ dayjs().format('YYYY-MM-DD hh:mm') }}
        </a-descriptions-item>
        <a-descriptions-item label="数据ID">
          688
        </a-descriptions-item>
      </a-descriptions>
    </a-card>
  </a-card>
</template>

<style scoped lang="less">

</style>
