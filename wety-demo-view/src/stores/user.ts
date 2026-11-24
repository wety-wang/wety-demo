import type { MenuData } from '~@/layouts/basic-layout/typing'
import { logoutApi } from '~@/api/common/login'
import { getRouteMenusApi } from '~@/api/common/menu'
import { getUserInfoApi } from '~@/api/common/user'
import { useAccess } from '~@/composables/access'
import { rootRoute } from '~@/router/constant'
import { generateFlatRoutes, generateRoutes, generateTreeRoutes } from '~@/router/generate-route'
import { DYNAMIC_LOAD_WAY, DynamicLoadEnum } from '~@/utils/constant'

export const useUserStore = defineStore('user', () => {
  const routerData = shallowRef()
  const menuData = shallowRef<MenuData>([])
  const userInfo = shallowRef<{ id: number | string, username: string, nickname: string, avatar: string, roles?: (string | number)[] }>()
  const token = useAuthorization()
  const avatar = computed(() => userInfo.value?.avatar)
  const nickname = computed(() => userInfo.value?.nickname ?? userInfo.value?.username)
  const roles = computed(() => userInfo.value?.roles)

  const getMenuRoutes = async () => {
    // 后端菜单接口回退机制: 如果后端未提供菜单接口或请求失败，则回退到前端静态路由
    try {
      const data = await getRouteMenusApi()
      return generateTreeRoutes(data ?? [])
    }
    catch (err) {
      const fallback = await generateRoutes()
      return {
        menuData: fallback.menuData,
        routeData: fallback.routeData,
      }
    }
  }

  const generateDynamicRoutes = async () => {
    const dynamicLoadWay = DYNAMIC_LOAD_WAY === DynamicLoadEnum.BACKEND ? getMenuRoutes : generateRoutes
    // 统一接收返回结构（包含 menuData、routeData）
    const { menuData: treeMenuData, routeData } = await dynamicLoadWay()

    // 当后端动态加载菜单时，根据权限码 access 进行过滤
    const { hasAccess } = useAccess()
    const filterRoutesByAccess = (routes: any[]): any[] => {
      return routes
        .filter(route => !route.meta?.access || hasAccess(route.meta?.access))
        .map(route => ({
          ...route,
          children: route.children?.length ? filterRoutesByAccess(route.children) : route.children,
        }))
    }
    const filterMenusByAccess = (menus: any[]): any[] => {
      return menus
        .filter(menu => !menu.access || hasAccess(menu.access))
        .map(menu => ({
          ...menu,
          children: menu.children?.length ? filterMenusByAccess(menu.children) : menu.children,
        }))
    }

    const filteredRoutes = filterRoutesByAccess(routeData)
    menuData.value = filterMenusByAccess(treeMenuData)

    routerData.value = {
      ...rootRoute,
      children: generateFlatRoutes(filteredRoutes),
    }
    return routerData.value
  }

  // 获取用户信息
  const getUserInfo = async () => {
    // 获取后端账户信息，并进行前端结构映射
    const data = await getUserInfoApi()
    userInfo.value = {
      id: data?.code,
      username: data?.loginName,
      nickname: data?.userName,
      avatar: '',
      roles: data?.permissionCodes ?? data?.roleCodes ?? [],
    }
  }

  const logout = async () => {
    // 退出登录
    // 1. 清空用户信息
    try {
      await logoutApi()
    }
    finally {
      token.value = null
      userInfo.value = undefined
      routerData.value = undefined
      menuData.value = []
    }
  }

  return {
    userInfo,
    roles,
    getUserInfo,
    logout,
    routerData,
    menuData,
    generateDynamicRoutes,
    avatar,
    nickname,
  }
})
