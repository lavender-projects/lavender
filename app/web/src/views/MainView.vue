<template>
  <div>
    <div class="content">
      <RouterView />
    </div>
    <van-tabbar route>
      <van-tabbar-item replace to="/recommendedVideo" icon="video-o">视频</van-tabbar-item>
      <van-tabbar-item replace to="/dynamic" icon="friends-o">动态</van-tabbar-item>
      <van-tabbar-item replace to="/settings" icon="setting-o">设置</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>

</script>

<style scoped lang="scss">
* {
  --van-tabbar-height: 50px;
}

.content {
  height: calc(100vh - var(--van-tabbar-height));
}

::v-deep(.van-icon:before) {
  font-size: 23px;
}

::v-deep(.van-tabbar-item__text) {
  font-size: 10.25px;
}
</style>