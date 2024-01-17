import { createRouter, createWebHistory } from 'vue-router'
import MainView from '@/views/MainView.vue'
import RecommendedVideoView from '@/views/RecommendedVideoView.vue'
import SettingsView from '@/views/SettingsView.vue'
import DynamicView from '@/views/DynamicView.vue'
import VideoPlayingView from '@/views/VideoPlayingView.vue'
import settingsRoutes from '@/router/routes/settings'

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
    {
      path: '/videoPlaying',
      component: VideoPlayingView,
      props: route => ({ ...route.query })
    },
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
