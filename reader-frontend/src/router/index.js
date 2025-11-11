import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/reader/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/reader/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reader/detail/:id',
    name: 'BookDetail',
    component: () => import('@/views/BookDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reader/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reader',
    redirect: '/reader/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/reader/login')
  } else if (to.path === '/reader/login' && userStore.token) {
    next('/reader/home')
  } else {
    next()
  }
})

export default router

