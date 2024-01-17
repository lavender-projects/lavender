import request from '@/utils/request'

const videoApi = {
  recommendedVideoList: () => request({
    url: '/video/recommended',
    method: 'get'
  }),
  videoDetail: params => request({
    url: '/video/detail',
    method: 'get',
    params
  }),
  commentList: params => request({
    url: '/video/comment/list',
    method: 'get',
    params
  }),
  commentReplyList: params => request({
    url: '/video/comment/reply/list',
    method: 'get',
    params
  }),
  danmakuList: params => request({
    url: '/video/danmaku/list',
    method: 'get',
    params
  }),
  episodeInfoList: params => request({
    url: '/video/episode/list',
    method: 'get',
    params
  }),
  streamInfoList: params => request({
    url: '/video/stream/urlList',
    method: 'get',
    params
  })
}

export default videoApi