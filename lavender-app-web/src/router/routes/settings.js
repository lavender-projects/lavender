import InformationSourceSettingsView from '@/views/settings/InformationSourceSettingsView.vue'
import BilibiliInformationSourceSettingsView
  from '@/views/settings/informationSource/BilibiliInformationSourceSettingsView.vue'
import BilibiliLoginView from '@/views/settings/login/BilibiliLoginView.vue'

const settingsRoutes = [
  {
    path: '/settings/informationSource',
    component: InformationSourceSettingsView,
  },
  {
    path: '/settings/informationSource/bilibili',
    component: BilibiliInformationSourceSettingsView
  },
  {
    path: '/settings/informationSource/bilibili/login',
    component: BilibiliLoginView
  }
]

export default settingsRoutes