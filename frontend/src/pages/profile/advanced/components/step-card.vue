<script setup lang="ts">
import { DingdingOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const { isMobile } = useQueryBreakpoints()
</script>

<template>
  <!-- 标题：流程进度 -->
  <a-card title="流程进度" :bordered="false">
    <a-steps :current="1" :direction="isMobile && 'horizontal' || 'horizontal'" progress-dot>
      <a-step title="创建项目">
        <span class="text-3">创建项目</span>
        <template #description>
          <div class="text-12px align-left">
            <div>
              windlil
              <DingdingOutlined class="m-1 c-primary" />
            </div>
            <div>
              {{ dayjs().subtract(6, 'h').format('YYYY-MM-DD hh:mm') }}
            </div>
          </div>
        </template>
      </a-step>
      <a-step title="部门初审">
        <span class="text-3">部门初审</span>
        <template #description>
          <div class="text-12px">
            <div style="margin: 8px 0 4px">
              清风不问烟雨
              <DingdingOutlined class="m-1 c-primary" />
            </div>
            <div>
              <a-button type="link">
                催一下
              </a-button>
            </div>
          </div>
        </template>
      </a-step>
      <a-step title="财务复核">
        <span class="text-3">财务复核</span>
      </a-step>
      <a-step title="完成">
        <span class="text-3">完成</span>
      </a-step>
    </a-steps>
  </a-card>
</template>

<style scoped lang="less">

</style>
