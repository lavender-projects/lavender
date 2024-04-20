import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import codeUtils from '@/utils/code'
import { mockRequest } from '@/utils/request'

//noinspection JSUnusedGlobalSymbols
const fallbackDefinitions = {
  recommendedVideoList : {
    localMock: async () => await mockRequest({
      url: '/videoJsInterface/recommendedVideoList.json',
      method: 'get'
    }),
    remoteMock: async () => await codeUtils.remoteMockRequestAndGetData({
      url: '/video/recommended',
      method: 'get'
    })
  }
}

const methodDefinitions = {
  recommendedVideoList: {
    isAsync: true,
    fallback: fallbackDefinitions.recommendedVideoList.remoteMock,
  },
  videoDetails: {
    isAsync: true,
    fallback: async params => await codeUtils.remoteMockRequestAndGetData({
      url: '/video/details',
      method: 'get',
      params
    })
  },
  commentList: {
    isAsync: true,
    fallback: async params => await codeUtils.remoteMockRequestAndGetData({
      url: '/video/comment/list',
      method: 'get',
      params
    })
  },
  commentReplyList: {
    isAsync: true,
    fallback: async params => await codeUtils.remoteMockRequestAndGetData({
      url: '/video/comment/reply/list',
      method: 'get',
      params
    })
  },
  danmakuList: {
    isAsync: true,
    fallback: async params => await codeUtils.remoteMockRequestAndGetData({
      url: '/video/danmaku/list',
      method: 'get',
      params
    })
  },
  episodeInfoList: {
    isAsync: true,
    fallback: async params => await codeUtils.remoteMockRequestAndGetData({
      url: '/video/episode/list',
      method: 'get',
      params
    })
  },
  streamInfoList: {
    isAsync: true,
    fallback: async params => await codeUtils.remoteMockRequestAndGetData({
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