import request from '@/utils/request'

const validateCodeApi = {
  bilibiliValidateCode: () => request({
    url: '/bilibili/validateCode',
    method: 'get'
  })
}

export default validateCodeApi