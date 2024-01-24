import LavsourceSettingsView from '@/views/settings/lavsource/LavsourceSettingsView.vue'
import LavsourceAddLocalView from '@/views/settings/lavsource/LavsourceAddLocalView.vue'
import LavsourceAddNetworkView from '@/views/settings/lavsource/LavsourceAddNetworkView.vue'

const lavsourceSettingsRoutes = [
  {
    path: '/settings/lavsource',
    component: LavsourceSettingsView
  },
  {
    path: '/settings/lavsource/addLocal',
    component: LavsourceAddLocalView
  },
  {
    path: '/settings/lavsource/addNetwork',
    component: LavsourceAddNetworkView
  }
]

export default lavsourceSettingsRoutes