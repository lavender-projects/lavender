const codeUtils = {
  sleep: timeMillis => new Promise(res => setTimeout(res, timeMillis)),
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
  randomUUID: () => {
    let s = []
    let hexDigits = '0123456789abcdef'
    for(let i = 0; i < 36; i++) {
      s[i] = hexDigits.substring(Math.floor(Math.random() * 0x10), 1)
    }
    s[14] = '4'
    s[19] = hexDigits.substring((s[19] & 0x3) | 0x8, 1)
    s[8] = s[13] = s[18] = s[23] = '-'
    return s.join('')
  }
}

export default codeUtils