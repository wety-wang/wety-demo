// 简单封装加密工具，提供 SM2 加密能力
// 说明：后端使用了国密 SM2 加密算法（Java版 sm-crypto 对应实现），
// 前端使用 npm 包 `sm-crypto` 来对登录密码进行加密，确保后端能正确解密。

import { sm2 } from 'sm-crypto'

// 从后端代码中的 SM2Utils.java 中复制的公钥字符串
// 注意：公钥不是敏感信息，可以安全地在前端使用
// 如果后续后端改为动态下发公钥，可改为请求配置接口获取
export const SM2_PUBLIC_KEY
  // 必须与后端 SM2Utils.PUBLIC_KEY 保持完全一致（非压缩点，前缀 04）
  // 后端路径：wety-demo-api/src/main/java/com/wety/demo/common/util/SM2Utils.java
  = '0496801ca6cbe79ab75dc0a396e19d90cbaa248e1c1c990c49fce2912a73c0308862323dea0f96f3a8b4e5c19270a4c52242db5dbff9d1df593998ec442ec2b0a6'

// 使用 SM2 公钥对明文进行加密
export function encryptSM2(plainText: string): string {
  // 调用 sm-crypto 的 SM2 加密方法
  // 与后端保持同一模式：cipherMode=0（C1C2C3），输出十六进制密文
  return sm2.doEncrypt(plainText, SM2_PUBLIC_KEY, 0)
}
