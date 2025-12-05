// 获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : {}
}

// 检查用户是否有指定角色
export const hasRole = (role) => {
  const userInfo = getUserInfo()
  return userInfo.role === role
}
