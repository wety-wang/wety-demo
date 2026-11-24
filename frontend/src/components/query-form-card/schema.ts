// 通用查询表单项 Schema 类型定义
// 说明：组件化抽象，用于各种查询场景（表格或非表格）
export interface FormItemSchema {
  // 字段名，与查询参数键一致
  name: string
  // 标签文本
  label: string
  // 组件类型：支持多类输入组件
  // input：输入框；select：下拉选择；radio：单选；checkbox：多选；date：日期；daterange：日期范围；datetime：日期时间；time：时间；treeSelect：树形下拉选择
  type?: 'input' | 'select' | 'radio' | 'checkbox' | 'date' | 'daterange' | 'datetime' | 'time' | 'treeSelect'
  // 占位提示
  placeholder?: string
  // 栅格宽度（默认 6，对应 1/4）
  span?: number
  // 是否为高级字段（默认 false；展开时显示）
  advanced?: boolean
  // 组件额外属性（透传给具体的 a-* 组件）
  componentProps?: Record<string, any>
  // 选项配置：用于 select/radio/checkbox；对于 treeSelect，支持嵌套 children 形成树
  options?: Array<{ label: string, value: any, children?: any[] }>
  // 是否必填（简单必填校验）
  required?: boolean
  // 自定义校验规则（透传给 a-form-item）
  rules?: Array<Record<string, any>>
  // 值传递策略（仅对 treeSelect 生效）'leaf'：仅传递叶子节点值（默认）；'all'：传递所有选中节点（含父级）
  valueStrategy?: 'leaf' | 'all'
}
