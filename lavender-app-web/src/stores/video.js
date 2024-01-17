import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useRecommendedVideoListStore = defineStore('recommendVideoList', () => {
  const videoItems = ref([])
  const nowPosition = ref(0)

  return { videoItems, nowPosition }
})