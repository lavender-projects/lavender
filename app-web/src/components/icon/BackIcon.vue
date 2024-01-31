<template>
  <div class="icon-container">
    <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="200" height="200">
      <path d="M800 480H268.8l233.6-233.6c12.8-12.8 12.8-32 0-44.8-12.8-12.8-32-12.8-44.8 0l-284.8 288c-12.8 12.8-12.8 32 0 44.8h3.2l284.8 288c6.4 6.4 16 9.6 22.4 9.6 9.6 0 16-3.2 22.4-9.6 12.8-12.8 12.8-32 0-44.8L272 544H800c19.2 0 32-12.8 32-32s-16-32-32-32z"
            :fill="fill" />
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  color: String
})

const fill = computed(() => props.color ? props.color : 'white')
</script>

<style scoped lang="scss">

</style>