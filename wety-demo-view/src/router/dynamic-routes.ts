import type { RouteRecordRaw } from 'vue-router'
import { AccessEnum } from '~@/utils/constant'
import { basicRouteMap } from './router-modules'

export default [
  {
    path: '/dashboard',
    redirect: '/dashboard/analysis',
    name: 'Dashboard',
    meta: {
      title: '仪表盘',
      icon: 'DashboardOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/dashboard/analysis',
        name: 'DashboardAnalysis',
        component: () => import('~/pages/dashboard/analysis/index.vue'),
        meta: {
          title: '分析页',
        },
      },
      {
        path: '/dashboard/monitor',
        name: 'DashboardMonitor',
        component: () => import('~/pages/dashboard/monitor/index.vue'),
        meta: {
          title: '监控页',
        },
      },
      {
        path: '/dashboard/workplace',
        name: 'DashboardWorkplace',
        component: () => import('~/pages/dashboard/workplace/index.vue'),
        meta: {
          title: '监控页',
        },
      },
    ],
  },
  {
    path: '/form',
    redirect: '/form/basic-form',
    name: 'Form',
    meta: {
      title: '表单页',
      icon: 'FormOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/form/basic-form',
        name: 'FormBasic',
        component: () => import('~/pages/form/basic-form/index.vue'),
        meta: {
          title: '基础表单',
        },
      },
      {
        path: '/form/step-form',
        name: 'FormStep',
        component: () => import('~/pages/form/step-form/index.vue'),
        meta: {
          title: '分步表单',
        },
      },
      {
        path: '/form/advanced-form',
        name: 'FormAdvanced',
        component: () => import('~/pages/form/advanced-form/index.vue'),
        meta: {
          title: '高级表单',
        },
      },
    ],
  },
  {
    path: '/link',
    redirect: '/link/iframe',
    name: 'Link',
    meta: {
      title: '链接',
      icon: 'LinkOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/link/iframe',
        name: 'LinkIframe',
        component: basicRouteMap.Iframe,
        meta: {
          title: 'AntDesign',
          url: 'https://ant.design/',
        },
      },
      {
        path: '/link/antdv',
        name: 'LinkAntdv',
        component: basicRouteMap.Iframe,
        meta: {
          title: 'AntDesignVue',
          url: 'https://antdv.com/',
        },
      },
      {
        path: 'https://www.baidu.com',
        name: 'LinkExternal',
        meta: {
          title: '跳转百度',
          // target: '_self',
        },
      },
    ],
  },
  {
    path: '/menu',
    redirect: '/menu/menu1',
    name: 'Menu',
    meta: {
      title: '菜单',
      icon: 'BarsOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/menu/menu1',
        name: 'MenuMenu11',
        component: () => import('~/pages/menu/menu1.vue'),
        meta: {
          title: '菜单1',
        },
      },
      {
        path: '/menu/menu2',
        name: 'MenuMenu12',
        component: () => import('~/pages/menu/menu2.vue'),
        meta: {
          title: '菜单2',
        },
      },
      {
        path: '/menu/menu3',
        redirect: '/menu/menu3/menu1',
        name: 'MenuMenu1-1',
        meta: {
          title: '菜单1-1',
        },
        children: [
          {
            path: '/menu/menu3/menu1',
            name: 'MenuMenu111',
            component: () => import('~/pages/menu/menu-1-1/menu1.vue'),
            meta: {
              title: '菜单1-1-1',
            },
          },
          {
            path: '/menu/menu3/menu2',
            name: 'MenuMenu112',
            component: () => import('~/pages/menu/menu-1-1/menu2.vue'),
            meta: {
              title: '菜单1-1-2',
            },
          },
        ],
      },
      {
        path: '/menu/menu4',
        redirect: '/menu/menu4/menu1',
        name: 'MenuMenu2-1',
        meta: {
          title: '菜单2-1',
        },
        children: [
          {
            path: '/menu/menu4/menu1',
            name: 'MenuMenu211',
            component: () => import('~/pages/menu/menu-2-1/menu1.vue'),
            meta: {
              title: '菜单2-1-1',
            },
          },
          {
            path: '/menu/menu4/menu2',
            name: 'MenuMenu212',
            component: () => import('~/pages/menu/menu-2-1/menu2.vue'),
            meta: {
              title: '菜单2-1-2',
            },
          },
        ],
      },
    ],
  },
  {
    path: '/profile',
    name: 'profile',
    redirect: '/profile/basic',
    meta: {
      title: '详情页',
      icon: 'ProfileOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/profile/basic',
        name: 'ProfileBasic',
        component: () => import('~/pages/profile/basic/index.vue'),
        meta: {
          title: '基础详情页',
        },
      },
    ],
  },
  {
    path: '/access',
    redirect: '/access/common',
    name: 'Access',
    meta: {
      title: '权限模块',
      icon: 'ClusterOutlined',
    },
    children: [
      {
        path: '/access/common',
        name: 'AccessCommon',
        component: () => import('~/pages/access/common.vue'),
        meta: {
          title: '通用权限',
        },
      },
      {
        path: '/access/user',
        name: 'AccessUser',
        component: () => import('~/pages/access/user.vue'),
        meta: {
          title: '普通用户',
          access: [AccessEnum.USER, AccessEnum.ADMIN],
        },
      },
      {
        path: '/access/admin',
        name: 'AccessAdmin',
        component: () => import('~/pages/access/admin.vue'),
        meta: {
          title: '管理员',
          access: [AccessEnum.ADMIN],
        },
      },
    ],
  },
  {
    path: '/exception',
    redirect: '/exception/403',
    name: 'Exception',
    meta: {
      title: '异常页',
      icon: 'WarningOutlined',
    },
    children: [
      {
        path: '/exception/403',
        name: 'Exception403',
        component: () => import('~/pages/exception/403.vue'),
        meta: {
          title: '403',
        },
      },
      {
        path: '/exception/404',
        name: 'Exception404',
        component: () => import('~/pages/exception/404.vue'),
        meta: {
          title: '404',
        },
      },
      {
        path: '/exception/500',
        name: 'Exception500',
        component: () => import('~/pages/exception/500.vue'),
        meta: {
          title: '500',
        },
      },
    ],
  },
  // 结果页
  {
    path: '/result',
    redirect: '/result/success',
    name: 'Result',
    meta: {
      title: '结果页',
      icon: 'CheckCircleOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/result/success',
        name: 'ResultSuccess',
        component: () => import('~/pages/result/success.vue'),
        meta: {
          title: '成功页',
        },
      },
      {
        path: '/result/fail',
        name: 'ResultFail',
        component: () => import('~/pages/result/fail.vue'),
        meta: {
          title: '失败页',
        },
      },
    ],
  },
  {
    path: '/list',
    redirect: '/list/card-list',
    name: 'List',
    meta: {
      title: '列表页',
      icon: 'TableOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/list/card-list',
        name: 'CardList',
        component: () => import('~/pages/list/card-list.vue'),
        meta: {
          title: '卡片列表',
        },
      },
      {
        path: '/list/table-list',
        name: 'ConsultTable',
        component: () => import('~/pages/list/table-list.vue'),
        meta: {
          title: '查询表格',
        },
      },
      {
        path: '/list/crud-table',
        name: 'CrudTable',
        component: () => import('~/pages/list/crud-table.vue'),
        meta: {
          title: '增删改查表格',
        },
      },
      {
        path: '/list/basic-list',
        name: 'BasicList',
        component: () => import('~/pages/list/basic-list.vue'),
        meta: {
          title: '标准列表',
        },
      },
      {
        path: '/list/search-list',
        name: 'SearchList',
        component: () => import('~/pages/list/search-list/index.vue'),
        meta: {
          title: '搜索列表',
        },
        redirect: '/list/search-list/articles',
        children: [
          {
            path: '/list/search-list/articles',
            name: 'SearchListArticles',
            component: () => import('~/pages/list/search-list/articles.vue'),
            meta: {
              title: '搜索列表（文章）',
            },
          },
          {
            path: '/list/search-list/projects',
            name: 'SearchListProjects',
            component: () => import('~/pages/list/search-list/projects.vue'),
            meta: {
              title: '搜索列表（项目）',
            },
          },
          {
            path: '/list/search-list/applications',
            name: 'SearchListApplications',
            component: () => import('~/pages/list/search-list/applications.vue'),
            meta: {
              title: '搜索列表（应用）',
            },
          },
        ],
      },
    ],
  },
  // 组织架构
  {
    path: '/org',
    redirect: '/org/dept',
    name: 'Org',
    meta: {
      title: '组织架构',
      icon: 'ApartmentOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/org/dept',
        name: 'OrgDeptManage',
        component: () => import('~/pages/org/dept/index.vue'),
        meta: {
          title: '部门管理',
          keepAlive: true,
        },
      },
      {
        path: '/org/position',
        name: 'OrgPositionManage',
        component: () => import('~/pages/org/position/index.vue'),
        meta: {
          title: '岗位管理',
          keepAlive: true,
        },
      },
      {
        path: '/org/user',
        name: 'OrgUserManage',
        component: () => import('~/pages/org/user/index.vue'),
        meta: {
          title: '用户管理',
          keepAlive: true,
        },
      },
      {
        path: '/org/role',
        name: 'OrgRoleManage',
        component: () => import('~/pages/org/role/index.vue'),
        meta: {
          title: '角色管理',
          keepAlive: true,
        },
      },
    ],
  },
  // 审计日志
  {
    path: '/audit-log',
    redirect: '/audit-log/login',
    name: 'AuditLog',
    meta: {
      title: '审计日志',
      icon: 'AuditOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/audit-log/login',
        name: 'AuditLogLoginLog',
        component: () => import('~/pages/audit-log/login-log/index.vue'),
        meta: {
          title: '登录日志',
          keepAlive: true,
        },
      },
      {
        path: '/audit-log/operation',
        name: 'AuditLogOperationLog',
        component: () => import('~/pages/audit-log/operation-log/index.vue'),
        meta: {
          title: '操作日志',
          keepAlive: true,
        },
      },
    ],
  },
  {
    path: '/account',
    redirect: '/account/center',
    name: 'Account',
    meta: {
      title: '个人页',
      icon: 'UserOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/account/center',
        name: 'AccountCenter',
        component: () => import('~/pages/account/center.vue'),
        meta: {
          title: '个人主页',
        },
      },
      {
        path: '/account/settings',
        name: 'AccountSettings',
        component: () => import('~/pages/account/settings.vue'),
        meta: {
          title: '个人设置',
        },
      },
      {
        path: '/account/settings/:id',
        name: 'AccountSettings1',
        component: () => import('~/pages/account/settings.vue'),
        meta: {
          title: '个人设置1',
          hideInMenu: true,
          parentKeys: ['/account/settings'],
        },
      },
    ],
  },
] as RouteRecordRaw[]
