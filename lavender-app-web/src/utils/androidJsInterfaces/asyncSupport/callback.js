const jsInterfaceAsyncMethodCallbacks = {}

//noinspection JSUnusedGlobalSymbols
const jsInterfaceAsyncMethodCallbackUtils = {
  //仅main.js中调用一次
  exposeToGlobal() {
    window.jsInterfaceAsyncMethodCallbackUtils = this
  },
  invokeCallback(id, resultObj) {
    let callback = jsInterfaceAsyncMethodCallbacks[id]
    if(callback == null) return
    callback(resultObj)
  },
  addCallback(id, resolve, reject) {
    jsInterfaceAsyncMethodCallbacks[id] = resultObj => {
      this.removeCallback(id)
      resultObj = resultObj ?? {
        isResolve: false,
        isPlainText: true,
        result: ''
      }
      if(resultObj.isResolve === true) {
        resolve(resultObj.isPlainText ? resultObj.result : JSON.parse(resultObj.result))
        return
      }
      reject(resultObj.isPlainText ? resultObj.result : JSON.parse(resultObj.result))
    }
  },
  removeCallback(id) {
    delete jsInterfaceAsyncMethodCallbacks[id]
  }
}

export default jsInterfaceAsyncMethodCallbackUtils