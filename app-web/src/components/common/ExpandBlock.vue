<template>
  <div class="expand-block" ref="expandBlockDom">
    <div class="content" ref="contentDom">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import codeUtils from '@/utils/code'

const props = defineProps({
  expand: Boolean,
  beforeExpandChange: Function,
  afterExpandChange: Function
})

const expandBlockDom = ref()

const contentDom = ref()

const contentHeight = ref('')

const cachedAccurateContentHeight = ref(null)

watch(() => props.expand, () => {
  onExpandChanged()
})

async function onExpandChanged() {
  if(props.beforeExpandChange != null) {
    await props.beforeExpandChange()
  }
  let expand = props.expand ?? false
  if(expand) {
    expandBlockDom.value.style.height = contentHeight.value
  } else {
    //通过这种方法可以移除style属性里的某个样式的值
    expandBlockDom.value.style.height = ''
  }
  //height刚刚被改变时，CSS动画也刚刚被触发
  //故这个方法被调用的时机，是CSS动画刚刚开始时
  if(props.afterExpandChange != null) {
    await props.afterExpandChange()
  }
}

function getContentDom() {
  return contentDom.value
}

/**
 * 计算目前的内容高度值（当内容高度在某个时间段内确定不变时调用）
 */
function calcContentHeight() {
  contentHeight.value = window.getComputedStyle(contentDom.value).height
}

//此方法不一定能计算出正确的内容高度值
//能否计算出准确的高度值，取决于内容区域的高度，需要花多长时间才能保持稳定
async function calcAccurateContentHeight() {
  if(cachedAccurateContentHeight.value != null) {
    return cachedAccurateContentHeight.value
  }
  calcContentHeight()
  //检测高度值是否发生变化
  let sameValueTimes = 0
  for(let i = 0; i < 100; i++) {
    await codeUtils.sleep(10)
    let oldHeight = contentHeight.value
    calcContentHeight()
    if(contentHeight.value === oldHeight) {
      sameValueTimes++
      if(sameValueTimes >= 10) break
    } else {
      sameValueTimes = 0
    }
  }
  cachedAccurateContentHeight.value = contentHeight.value
}

function getCachedAccurateContentHeight() {
  return cachedAccurateContentHeight.value
}

defineExpose({
  getContentDom,
  calcContentHeight,
  calcAccurateContentHeight,
  getCachedAccurateContentHeight
})
</script>

<style scoped lang="scss">
.expand-block {
  overflow-y: hidden;
  transition: 0.3s height;
}
</style>