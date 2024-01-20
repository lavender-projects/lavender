import basicJsInterface from '@/utils/androidJsInterfaces/definition/basicJsInterface'
import jsInterfaceAsyncMethodCallbackUtils from '@/utils/androidJsInterfaces/asyncSupport/callback'

const jsInterfaceAsyncSupportUtils = {
  getAsyncMethodStub: (jsInterfaceName, methodName) => {
    return (...args) => new Promise((resolve, reject) => {
      let callbackId = basicJsInterface.getUuid()
      jsInterfaceAsyncMethodCallbackUtils.addCallback(callbackId, resolve, reject)
      //noinspection JSUnresolvedReference
      window['android_AsyncTaskJsInterface'].invokeAsyncMethod(jsInterfaceName, methodName, callbackId, JSON.stringify(args))
    })
  }
}

export default jsInterfaceAsyncSupportUtils