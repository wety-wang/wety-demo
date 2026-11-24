
## 介绍

本项目基于AntdvPro开发。

AntdvPro是一个基于Vue3、Vite4、ant-design-vue4、Pinia、UnoCSS和Typescript的一整套企业级中后台前端/设计解决方案，它参考了阿里react版本antd-pro的设计模式，使用了最新最流行的前端技术栈，内置了动态路由、多主题、多布局等功能，可以帮助你快速搭建企业级中后台产品原型。

[antdv-pro-docs](https://docs.antdv-pro.com) - 在线文档地址

## 使用

```bash
# 安装依赖
pnpm install

# 启动项目
pnpm dev
```

## 部署指南（环境变量与 Nginx 代理）

### 环境变量说明

- `VITE_APP_BASE_API=/api`
  - 统一接口前缀，开发环境由 Vite 代理到后端，生产环境由 Nginx 反向代理到后端。
- `VITE_APP_BASE_URL_DEV=http://127.0.0.1:8080`
  - 本地开发后端地址（Vite 开发代理目标）。
- `VITE_APP_LOAD_ROUTE_WAY=FRONTEND | BACKEND`
  - 路由加载方式：前端/后端。

注意：已清理未使用变量 `VITE_APP_BASE_API_DEV` 与 `VITE_APP_BASE_URL`，请勿再配置以避免歧义。

### 生产环境 Nginx 反向代理示例

```nginx
# 关键节点：前端与后端统一以 /api 为前缀进行通信
# - 前端构建后的静态资源由 Nginx 提供
# - /api 前缀的请求由 Nginx 代理到后端服务（如 127.0.0.1:8080）

server {
    listen 80;                        # 监听端口
    server_name example.com;          # 替换为你的域名

    # 前端静态资源目录（替换为你的打包目录路径）
    root   /var/www/antdv-pro/dist;   # 前端打包输出目录
    index  index.html;

    # 前端路由处理（history 模式）
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 统一接口前缀代理：将 /api 转发到后端服务
    location /api/ {
        # 关键节点：proxy_pass 结尾以 / 结尾，避免路径重复
        proxy_pass http://127.0.0.1:8080/;

        # 透传真实客户端信息（可选但推荐）
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 超时与连接优化（按需调整）
        proxy_connect_timeout 5s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
}
```

验证要点：
- 构建后前端请求例如 `POST /api/bac/common/login`，后端应收到 `POST /bac/common/login`。
- 登录页验证码请求应为 `GET /api/bac/common/getVerifyImage?ts=...`。
- 若后端为 HTTPS 或跨域，请相应调整 `proxy_pass` 与证书配置。
