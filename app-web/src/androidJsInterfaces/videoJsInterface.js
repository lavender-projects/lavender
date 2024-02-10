import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import request from '@/utils/request'

const methodDefinitions = {
  recommendedVideoList: {
    isAsync: true,
    fallback: () => request({
      url: '/video/recommended',
      method: 'get'
    })
  },
  videoDetail: {
    isAsync: true,
    fallback: params => request({
      url: '/video/detail',
      method: 'get',
      params
    })
  },
  commentList: {
    isAsync: true,
    fallback: params => request({
      url: '/video/comment/list',
      method: 'get',
      params
    })
  },
  commentReplyList: {
    isAsync: true,
    fallback: params => request({
      url: '/video/comment/reply/list',
      method: 'get',
      params
    })
  },
  danmakuList: {
    isAsync: true,
    fallback: params => request({
      url: '/video/danmaku/list',
      method: 'get',
      params
    })
  },
  episodeInfoList: {
    isAsync: true,
    fallback: params => request({
      url: '/video/episode/list',
      method: 'get',
      params
    })
  },
  streamInfoList: {
    isAsync: true,
    fallback: params => request({
      url: '/video/stream/urlList',
      method: 'get',
      params
    })
  }
}

const videoJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'VideoJsInterface', methodDefinitions
) ?? methodDefinitions

export default videoJsInterface