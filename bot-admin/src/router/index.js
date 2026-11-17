import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/login.vue'
import Index from '../views/index.vue'
import Layout from '../views/layout.vue'
import BotStatus from '../views/status/BotStatus.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Layout,
      children: [
        { path: '/index', component: Index  },
        { path: '/status', component: BotStatus  },
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
  ],
})

export default router
