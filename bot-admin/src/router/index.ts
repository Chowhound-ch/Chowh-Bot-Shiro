import { createRouter, createWebHashHistory } from 'vue-router'
import AdminView from '../views/AdminView.vue'

export default createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', redirect: '/overview' },
    { path: '/:section(overview|dictionary|kfc|subscriptions|logs)', component: AdminView },
    { path: '/:pathMatch(.*)*', redirect: '/overview' },
  ],
})
