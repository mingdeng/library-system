import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/admin',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'books',
        name: 'BookManagement',
        component: () => import('@/views/BookManagement.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue')
      }
    ]
  },
  {
    path: '/admin',
    redirect: '/admin/login'
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
    next('/admin/login')
  } else if (to.path === '/admin/login' && userStore.token) {
    next('/admin/dashboard')
  } else {
    next()
  }
})

export default router

