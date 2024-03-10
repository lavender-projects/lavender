import { createRouter, createWebHistory } from 'vue-router'
import MainView from '@/views/MainView.vue'
import RecommendedVideoView from '@/views/RecommendedVideoView.vue'
import SettingsView from '@/views/SettingsView.vue'
import DynamicView from '@/views/DynamicView.vue'
import settingsRoutes from '@/router/routes/settings'
import videoRoutes from '@/router/routes/video'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainView,
      children: [
        {
          path: '/recommendedVideo',
          component: RecommendedVideoView
        },
        {
          path: '/dynamic',
          component: DynamicView
        },
        {
          path: '/settings',
          component: SettingsView
        }
      ]
    },
    ...videoRoutes,
    ...settingsRoutes
  ]
})

router.beforeEach((to, from, next) => {
  if(to.path === '/') {
    next('/recommendedVideo')
    return
  }
  next()
})

export default router
