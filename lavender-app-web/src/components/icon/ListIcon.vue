<template>
  <div class="icon-container">
    <svg class="icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="200" height="200">
      <path d="M111.395066 179.64038l801.208844 0 0 87.866187-801.208844 0 0-87.866187Z" :fill="fill" />
      <path d="M111.395066 468.067418l801.208844 0 0 87.866187-801.208844 0 0-87.866187Z" :fill="fill" />
      <path d="M111.395066 756.493433l801.208844 0 0 87.866187-801.208844 0 0-87.866187Z" :fill="fill" />
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