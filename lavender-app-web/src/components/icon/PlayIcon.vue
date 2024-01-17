<template>
  <div class="icon-container">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" :fill="fill">
      <path d="M6 2.914v18.172L20.279 12 6 2.914z" :fill="fill" />
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