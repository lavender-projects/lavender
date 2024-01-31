<!--suppress CssNonIntegerLengthInPixels -->
<template>
  <div class="recommended-video-item">
    <div class="card">
      <div class="cover">
        <img class="cover-img" :src="coverImg" alt="" />
        <div class="video-data">
          <div class="left">
            <play-count-icon class="icon" />
            <div style="margin-left: 2.5px;">{{ playCount }}</div>
            <danmaku-icon class="icon" style="margin-left: 8px;" />
            <div class="danmaku">{{ danmakuCount }}</div>
          </div>
          <div class="right">
            <span>{{ duration }}</span>
          </div>
        </div>
      </div>
      <div class="title">
        <div class="van-multi-ellipsis--l2 text">{{ title }}</div>
        <div class="author">
          <div class="left">
            <author-icon class="icon" color="rgb(148, 153, 160)" />
            <div class="van-ellipsis" style="margin-left: 2.5px;">{{ author }}</div>
          </div>
          <div class="right">
            <operations-icon class="operation-btn" color="rgb(148, 153, 160)" @click="onOperationClick" />
          </div>
        </div>
      </div>
      <div class="click-frame" @click="onVideoCardClick"></div>
    </div>
  </div>
</template>

<script setup>
import { showToast } from 'vant'
import PlayCountIcon from '@/components/icon/PlayCountIcon.vue'
import DanmakuIcon from '@/components/icon/DanmakuIcon.vue'
import AuthorIcon from '@/components/icon/AuthorIcon.vue'
import OperationsIcon from '@/components/icon/OperationsIcon.vue'
import basicJsInterface from '@/androidJsInterfaces/basicJsInterface'

const props = defineProps({
  coverImg: String,
  playCount: String,
  danmakuCount: String,
  duration: String,
  author: String,
  title: String,
  videoId: String
})

function onVideoCardClick() {
  basicJsInterface.openNewWebActivity(`/videoPlaying?videoId=${props.videoId}`)
}

function onOperationClick() {
  showToast('operation')
  console.log('operation', Date.now())
}
</script>

<style scoped lang="scss">
* {
  --cover-height: 63.5%;
}

.recommended-video-item {
  height: 225px;
  width: calc(50% - 7.5px);

  .card {
    background-color: white;
    width: 100%;
    height: 100%;
    position: relative;
    border-radius: var(--video-card-border-radius);

    .cover {
      width: 100%;
      height: var(--cover-height);
      position: relative;

      .cover-img {
        width: 100%;
        height: 100%;
        border-top-left-radius: var(--video-card-border-radius);
        border-top-right-radius: var(--video-card-border-radius);
        object-fit: cover;
      }

      .video-data {
        width: calc(100% - 14px);
        height: 20px;
        position: absolute;
        bottom: 0;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 15px 7px 3px;
        background-image: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, .8) 100%);
        color: white;
        font-size: 10px;

        .icon {
          width: 16.5px;
          height: 16.5px;
        }

        .left {
          display: flex;
          align-items: center;

          .danmaku {
            margin-left: 2.5px;
            position: relative;
            top: 0.3px;
          }
        }
      }
    }

    .title {
      width: calc(100% - 14px);
      height: calc(100% - var(--cover-height) - 10px);
      padding: 10px 7px 0;
      position: relative;

      .text {
        font-size: 14px;
        width: calc(100% - 6px);
        padding: 0 3px;
      }

      .author {
        position: absolute;
        bottom: 8px;
        --author-bar-height: 16.5px;
        height: var(--author-bar-height);
        width: calc(100% - 14px);
        display: flex;
        align-items: center;
        justify-content: space-between;

        .left {
          width: calc(100% - 20px);
          height: 100%;
          color: rgb(148, 153, 160);
          font-size: 12px;
          display: flex;
          align-items: center;

          .icon {
            width: var(--author-bar-height);
            height: 100%;
          }
        }

        .right {
          height: 100%;
          z-index: 11;

          .operation-btn {
            width: var(--author-bar-height);
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

.recommended-video-item:nth-child(odd) {
  padding: 5px 2.5px 0 5px;
}

.recommended-video-item:nth-child(even) {
  padding: 5px 5px 0 2.5px;
}
</style>