import VideoPlayingView from '@/views/VideoPlayingView.vue'

const videoRoutes = [
  {
    path: '/videoPlaying',
    component: VideoPlayingView,
    props: route => ({ ...route.query })
  }
]

export default videoRoutes