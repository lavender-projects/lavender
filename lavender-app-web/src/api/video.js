import request from '@/utils/request'

const videoApi = {
  recommendedVideoList: () => request({
    url: '/proxy/video/recommended',
    method: 'get'
  }),
  videoDetail: params => request({
    url: '/proxy/video/detail',
    method: 'get',
    params
  }),
  commentList: params => request({
    url: '/proxy/video/comment/list',
    method: 'get',
    params
  }),
  commentReplyList: params => request({
    url: '/proxy/video/comment/reply/list',
    method: 'get',
    params
  }),
  danmakuList: params => request({
    url: '/proxy/video/danmaku/list',
    method: 'get',
    params
  }),
  episodeInfoList: params => request({
    url: '/proxy/video/episode/list',
    method: 'get',
    params
  }),
  streamInfoList: params => request({
    url: '/proxy/video/stream/urlList',
    method: 'get',
    params
  })
}

export default videoApi