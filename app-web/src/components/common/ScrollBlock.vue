<template>
  <div class="scroll-block" ref="scrollBlockDom">
    <el-scrollbar :always="true">
      <slot></slot>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'

const props = defineProps({
  thumbWidth: {
    type: String,
    default: '3px'
  },
  scrollable: {
    type: Boolean,
    default: true
  }
})

const emits = defineEmits([ 'scroll', 'scrollEnd' ])

const scrollBlockDom = ref()

const scrollTop = ref(0)

/**
 * 真正设置了CSS的overflow属性的DOM
 */
const contentWrapperDom = computed(() => {
  if(scrollBlockDom.value == null) return null
  return scrollBlockDom.value.querySelector('.el-scrollbar__wrap')
})

const contentDom = computed(() => {
  if(scrollBlockDom.value == null) return null
  return scrollBlockDom.value.querySelector('.el-scrollbar__view')
})

let hideScrollBarTask

onMounted(() => {
  let verticalBar = scrollBlockDom.value.querySelector('.el-scrollbar__bar.is-vertical')
  let verticalThumb = verticalBar.querySelector('.el-scrollbar__thumb')
  setVerticalThumbFiller(verticalThumb)
  setVerticalThumbWidth(verticalBar, verticalThumb)
  disableVerticalThumbMouseEvent(verticalBar, verticalThumb)
  setScrollListener(verticalThumb)
  applyScrollableValue()
})

watch(() => props.scrollable, () => applyScrollableValue())

function setVerticalThumbFiller(thumb) {
  let filler = document.createElement('div')
  filler.classList.add('filler')
  thumb.appendChild(filler)
}

function setVerticalThumbWidth(bar, thumb) {
  let thumbWidth = props.thumbWidth
  bar.style.width = thumbWidth
  thumb.style.width = thumbWidth
}

function disableVerticalThumbMouseEvent(bar, thumb) {
  let disableEventCallback = e => e.stopPropagation()
  bar.addEventListener('mousedown', disableEventCallback, true)
  thumb.addEventListener('mousedown', disableEventCallback, true)
}

function setScrollListener(thumb) {
  let thumbFiller = thumb.querySelector('.filler')
  contentWrapperDom.value.addEventListener('scroll', () => onContentWrapperScroll(thumbFiller))
  contentWrapperDom.value.addEventListener('scrollend', onContentWrapperScrollEnd)
}

function applyScrollableValue() {
  contentWrapperDom.value.style.overflow = props.scrollable ? 'scroll' : 'hidden'
}

function onContentWrapperScroll(thumbFiller) {
  if(hideScrollBarTask != null) clearTimeout(hideScrollBarTask)
  if(!thumbFiller.classList.contains('show')) thumbFiller.classList.add('show')
  hideScrollBarTask = setTimeout(() => {
    thumbFiller.classList.remove('show')
  }, 2000)
  scrollTop.value = contentWrapperDom.value.scrollTop
  emits('scroll')
}

function onContentWrapperScrollEnd() {
  emits('scrollEnd')
}

//返回值为一个缓存值，与contentWrapperDom.value.scrollTop相同
function getScrollTopValue() {
  return scrollTop.value
}

function getMaxScrollTopValue() {
  //noinspection JSCheckFunctionSignatures
  let contentHeight = parseFloat(window.getComputedStyle(contentDom.value).height)
  //noinspection JSCheckFunctionSignatures
  let contentWrapperHeight = parseFloat(window.getComputedStyle(contentWrapperDom.value).height)
  return contentHeight - contentWrapperHeight
}

function contentWrapperScrollBy(distance) {
  contentWrapperDom.value.scrollBy(0, distance)
}

function getContentWrapperDom() {
  return contentWrapperDom.value
}

defineExpose({
  getScrollTopValue,
  getMaxScrollTopValue,
  contentWrapperScrollBy,
  getContentWrapperDom
})
</script>

<style scoped lang="scss">
.scroll-block {
  width: 100%;
  height: 100%;
}

::v-deep(.el-scrollbar__bar.is-horizontal) {
  display: none;
}

::v-deep(.el-scrollbar__bar.is-vertical) {
  z-index: 100;

  .el-scrollbar__thumb {
    background-color: unset;
    opacity: unset;
    cursor: unset;

    .filler {
      background-color: transparent;
      opacity: var(--el-scrollbar-opacity, 0.3);
      border-radius: inherit;
      height: 100%;
      width: 100%;
      transition: 0.3s background-color;
    }

    .filler.show {
      background-color: var(--el-scrollbar-bg-color, var(--el-text-color-secondary));
    }
  }

  .el-scrollbar__thumb:hover {
    background-color: unset;
    opacity: unset;
  }
}
</style>