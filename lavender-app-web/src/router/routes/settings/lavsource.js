import LavsourceSettingsView from '@/views/settings/lavsource/LavsourceSettingsView.vue'
import LavsourceAddView from '@/views/settings/lavsource/LavsourceAddView.vue'

const lavsourceSettingsRoutes = [
  {
    path: '/settings/lavsource',
    component: LavsourceSettingsView
  },
  {
    path: '/settings/lavsource/add',
    component: LavsourceAddView
  }
]

export default lavsourceSettingsRoutes