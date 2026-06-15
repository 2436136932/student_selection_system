import { useUserStore } from '../store/user'

// 获取用户信息
export const getUserInfo = () => {
  return useUserStore().userInfo
}

// 检查用户是否有指定角色
export const hasRole = (role) => {
  return useUserStore().hasRole(role)
}
