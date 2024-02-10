import messageUtils from '@/utils/message'

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
  addCallback(params) {
    jsInterfaceAsyncMethodCallbacks[params.id] = resultObj => {
      this.removeCallback(params.id)
      resultObj = resultObj ?? {
        isResolve: false,
        isPlainText: true,
        message: null,
        result: ''
      }
      if(resultObj.isResolve === true) {
        let result
        if(resultObj.isPlainText) {
          result = resultObj.result
        } else {
          result = resultObj.result != null ? JSON.parse(resultObj.result) : null
        }
        console.log(`${params.jsInterfaceName}.${params.methodName}()\nparams:`, params.args, '\nresult:', result)
        params.resolve(result)
        return
      }
      console.error('Android JavaScript Interface method error: \n', resultObj.message)
      messageUtils.error(resultObj.message)
      params.reject()
    }
  },
  removeCallback(id) {
    delete jsInterfaceAsyncMethodCallbacks[id]
  }
}

export default jsInterfaceAsyncMethodCallbackUtils