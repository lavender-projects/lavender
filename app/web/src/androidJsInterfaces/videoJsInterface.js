import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import codeUtils from '@/utils/code'

const methodDefinitions = {
  recommendedVideoList: {
    isAsync: true,
    fallback: async () => await codeUtils.mockRequestAndGetData({
      url: '/video/recommended',
      method: 'get'
    })
  },
  videoDetails: {
    isAsync: true,
    fallback: async params => await codeUtils.mockRequestAndGetData({
      url: '/video/details',
      method: 'get',
      params
    })
  },
  commentList: {
    isAsync: true,
    fallback: async params => await codeUtils.mockRequestAndGetData({
      url: '/video/comment/list',
      method: 'get',
      params
    })
  },
  commentReplyList: {
    isAsync: true,
    fallback: async params => await codeUtils.mockRequestAndGetData({
      url: '/video/comment/reply/list',
      method: 'get',
      params
    })
  },
  danmakuList: {
    isAsync: true,
    fallback: async params => await codeUtils.mockRequestAndGetData({
      url: '/video/danmaku/list',
      method: 'get',
      params
    })
  },
  episodeInfoList: {
    isAsync: true,
    fallback: async params => await codeUtils.mockRequestAndGetData({
      url: '/video/episode/list',
      method: 'get',
      params
    })
  },
  streamInfoList: {
    isAsync: true,
    fallback: async params => await codeUtils.mockRequestAndGetData({
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