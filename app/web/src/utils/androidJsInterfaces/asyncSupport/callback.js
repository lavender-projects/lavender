import messageUtils from '@/utils/message'

const asyncMethodCallTimeout = 10 * 1000

const jsInterfaceAsyncMethodCallbacks = {}

const jsInterfaceAsyncMethodCallbackRejectTasks = {}

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
      this.removeCallbackRejectTask(params.id)
      resultObj = resultObj ?? {
        isResolve: false,
        message: null,
        result: null
      }
      if(resultObj.isResolve === true) {
        console.log(`${params.jsInterfaceName}.${params.methodName}()\nparams:`, params.args, '\nresult:', resultObj.result)
        params.resolve(resultObj.result)
        return
      }
      console.error(`${params.jsInterfaceName}.${params.methodName}()\nparams:`, params.args, '\nerror:', resultObj.message)
      messageUtils.error(resultObj.message)
      params.reject()
    }
    jsInterfaceAsyncMethodCallbackRejectTasks[params.id] = setTimeout(() => {
      if(jsInterfaceAsyncMethodCallbacks[params.id] == null) return
      this.removeCallback(params.id)
      delete jsInterfaceAsyncMethodCallbackRejectTasks[params.id]
      console.error(
        `${params.jsInterfaceName}.${params.methodName}()\nparams:`,
        params.args,
        `\nerror: ${asyncMethodCallTimeout}ms Timeout`
      )
      messageUtils.error(`${asyncMethodCallTimeout}ms Timeout to call ${params.jsInterfaceName}.${params.methodName}()`)
      params.reject()
    }, asyncMethodCallTimeout)
  },
  removeCallback(id) {
    delete jsInterfaceAsyncMethodCallbacks[id]
  },
  removeCallbackRejectTask(id) {
    if(jsInterfaceAsyncMethodCallbackRejectTasks[id] == null) return
    clearTimeout(jsInterfaceAsyncMethodCallbackRejectTasks[id])
    delete jsInterfaceAsyncMethodCallbackRejectTasks[id]
  }
}

export default jsInterfaceAsyncMethodCallbackUtils