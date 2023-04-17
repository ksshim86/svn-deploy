/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'

// Plugins
import { registerPlugins } from '@/plugins'

import { useSdInfoStore } from './store/sdInfoStore'
import router from './router'

const app = createApp(App)

registerPlugins(app)

app.mount('#app')

const sdInfoStore = useSdInfoStore()
await sdInfoStore.fetchSdInfo()

// svn deploy 루트 경로가 설정되어 있는지 확인
if (!sdInfoStore.sdInfo) {
  router.push('/login')
} else {
  router.push('/setup')
}