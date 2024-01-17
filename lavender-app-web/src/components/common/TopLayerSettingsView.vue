<template>
  <div class="top-layer-settings-view">
    <van-nav-bar :title="title" left-arrow @click-left="back" />
    <div class="settings-view-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  title: String
})

function back() {
  history.back()
}
</script>

<style scoped lang="scss">

</style>