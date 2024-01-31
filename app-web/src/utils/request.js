import axios from 'axios'
import messageUtils from './message'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000
})

request.interceptors.request.use(config => {
  return config
}, error => {
  console.log(error)
  return Promise.reject(error)
})

request.interceptors.response.use(response => {
  const status = response.status
  const body = response.data
  if(status !== 200) {
    //noinspection JSUnresolvedReference
    return Promise.reject(new Error('API error: ' + body.msg))
  }
  return body
}, error => {
  console.log('Axios error: ', error)
  if(error.code === 'ERR_NETWORK') {
    messageUtils.error('网络请求失败')
    return Promise.reject(error)
  }
  let responseBody = error.response.data
  //noinspection JSUnresolvedReference
  let message = responseBody?.msg
  if(message != null && message !== '') {
    messageUtils.error(message)
  } else {
    messageUtils.error(error.message)
  }
  return Promise.reject(responseBody ?? error)
})

export default request