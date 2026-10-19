import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      //懒加载:只有访问该页面时才会加载内容(提升性能)
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
