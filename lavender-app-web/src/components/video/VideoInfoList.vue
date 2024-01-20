<template>
  <div class="video-info-list">
    <div class="item" v-for="item in props.videoInfoList">
      <div class="cover">
        <img :src="item.coverImg" alt="" />
        <div class="video-duration">{{ item.duration }}</div>
      </div>
      <div class="video-detail">
        <div class="top">
          <div class="title van-multi-ellipsis--l2">{{ item.title }}</div>
        </div>
        <div class="bottom">
          <div class="author">
            <author-icon class="icon" :color="componentParams.countIconColor" />
            <!--suppress CssNonIntegerLengthInPixels -->
            <div class="data van-ellipsis">{{ item.author }}</div>
          </div>
          <div class="counts">
            <div class="left">
              <play-count-icon class="icon" :color="componentParams.countIconColor" />
              <div class="data">{{ item.playCount }}</div>
              <danmaku-icon class="icon" :color="componentParams.countIconColor"
                            style="margin-left: 10px;" />
              <div class="data">{{ item.danmakuCount }}</div>
            </div>
            <div class="right">
              <operations-icon class="icon" :color="componentParams.countIconColor" />
            </div>
          </div>
        </div>
      </div>
      <div class="click-frame" @click="onVideoItemClick(item)"></div>
    </div>
  </div>
</template>

<script setup>
import OperationsIcon from '@/components/icon/OperationsIcon.vue'
import DanmakuIcon from '@/components/icon/DanmakuIcon.vue'
import AuthorIcon from '@/components/icon/AuthorIcon.vue'
import PlayCountIcon from '@/components/icon/PlayCountIcon.vue'
import { reactive } from 'vue'
import basicJsInterface from '@/utils/androidJsInterfaces/definition/basicJsInterface'

const props = defineProps({
  videoInfoList: Array
})

const componentParams = reactive({
  countIconColor: '#969799'
})

function onVideoItemClick(item) {
  basicJsInterface.openNewWebActivity(`/videoPlaying?videoId=${item.videoId}`)
}
</script>

<style scoped lang="scss">
.video-info-list {
  .item {
    display: flex;
    padding: 10px 0;
    margin-left: 11px;
    border-bottom: 1px solid var(--van-border-color);
    height: 72px;
    position: relative;

    .cover {
      width: 126px;
      height: 100%;
      position: relative;

      img {
        width: 100%;
        height: 100%;
        border-radius: 4px;
        object-fit: cover;
      }

      .video-duration {
        position: absolute;
        bottom: 4.6px;
        right: 4px;
        font-size: 10.4px;
        color: white;
        background-color: rgba(0, 0, 0, 0.4);
        padding: 1.5px 2.8px 1.2px;
        border-radius: 2.5px;
      }
    }

    .video-detail {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      width: calc(100vw - 126px - 10px);
      padding: 0 12px 0 8px;
      box-sizing: border-box;

      .top {
        .title {
          font-size: 14px;
          padding-right: 6px;
        }
      }

      .bottom {
        color: rgb(148, 153, 160);
        font-size: 12px;

        .icon {
          width: 16.5px;
          height: 100%;
        }

        .data {
          margin-left: 2px;
          height: 16.5px;
          line-height: 16.5px;
        }

        .author {
          display: flex;
          align-items: center;
          height: 16.5px;

          .icon {
            width: 16.5px;
            height: 100%;
          }
        }

        .counts {
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 16.5px;
          margin-top: 0.6px;

          .left {
            display: flex;
            align-items: center;
            height: 100%;

            .icon {
              position: relative;
              bottom: 0.2px;
            }
          }

          .right {
            height: 100%;
          }
        }
      }
    }

    .click-frame {
      position: absolute;
      top: 0;
      width: 100%;
      height: 100%;
      z-index: 10;
    }
  }
}
</style>