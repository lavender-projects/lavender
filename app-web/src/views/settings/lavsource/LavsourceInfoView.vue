<template>
  <top-layer-settings-view :title="`信源 - ${lavsourceInfo.name}`">

  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
import { onMounted, ref } from 'vue'

const lavsourceInfo = ref({})

onMounted(() => {
  lavsourceInfo.value = history.state.lavsourceInfo
})
</script>

<style scoped lang="scss">

</style>