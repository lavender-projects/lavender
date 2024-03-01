import request from '@/utils/request'

const codeUtils = {
  sleep: timeMillis => new Promise(resolve => setTimeout(resolve, timeMillis)),
  getDomHeight: dom => parseFloat(window.getComputedStyle(dom).height),
  async tryForResult(getter, times = 20, interval = 5) {
    let result
    let exception
    for(let i = 0; i < times; i++) {
      try {
        result = getter()
        break
      } catch(e) {
        exception = e
        await codeUtils.sleep(interval)
      }
    }
    if(result == null) throw exception
    return result
  },
  convertObjectWhichFromClassToNormal(object) {
    let clonedObject = {}
    for(let prop in object) {
      clonedObject[prop] = object[prop]
    }
    return clonedObject
  },
  requestAndGetData: async axiosParams => (await request(axiosParams)).data
}

export default codeUtils