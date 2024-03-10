import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'vant/lib/index.css'

//自定义样式需最后一个导入，以提高其优先级
import './assets/main.scss'
import { androidEventListenerUtils } from '@/utils/androidEventListeners'
import jsInterfaceAsyncMethodCallbackUtils from '@/utils/androidJsInterfaces/asyncSupport/callback'

androidEventListenerUtils.exposeToGlobal()
jsInterfaceAsyncMethodCallbackUtils.exposeToGlobal()

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')