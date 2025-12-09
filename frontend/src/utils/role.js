// 获取用户信息
export const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : {}
}

// 检查用户是否有指定角色
export const hasRole = (role) => {
  const userInfo = getUserInfo()
  console.log('hasRole函数被调用，传入角色:', role, '，当前用户角色:', userInfo.role);
  // 不区分大小写比较角色
  return userInfo.role && userInfo.role.toLowerCase() === role.toLowerCase()
}
