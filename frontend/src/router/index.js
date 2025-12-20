import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/home',
      name: 'Home',
      component: () => import('../views/HomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/students',
      name: 'Students',
      component: () => import('../views/StudentView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/standards',
      name: 'Standards',
      component: () => import('../views/StandardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/courses',
      name: 'Courses',
      component: () => import('../views/CourseView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/scores',
      name: 'Scores',
      component: () => import('../views/ScoreView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/awards',
      name: 'Awards',
      component: () => import('../views/AwardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'Users',
      component: () => import('../views/UserView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/teachers',
      name: 'Teachers',
      component: () => import('../views/TeacherView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/student-award-applications',
      name: 'StudentAwardApplications',
      component: () => import('../views/StudentAwardApplicationView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notices',
      name: 'Notices',
      component: () => import('../views/NoticeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/carousel',
      name: 'Carousel',
      component: () => import('../views/CarouselManage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/majors',
      name: 'Majors',
      component: () => import('../views/MajorManage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/system-settings',
      name: 'SystemSettings',
      component: () => import('../views/SystemSettingsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/chat',
      name: 'Chat',
      component: () => import('../views/ChatView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查页面是否需要认证
  if (to.meta.requiresAuth) {
    // 检查是否已登录
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      // 已登录，继续访问
      next()
    } else {
      // 未登录，跳转到登录页
      ElMessage.warning('请先登录')
      next('/')
    }
  } else {
    // 不需要认证的页面，直接访问
    next()
  }
})

export default router