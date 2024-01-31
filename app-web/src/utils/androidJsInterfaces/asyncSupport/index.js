import jsInterfaceAsyncMethodCallbackUtils from '@/utils/androidJsInterfaces/asyncSupport/callback'
import * as uuid from 'uuid'

const jsInterfaceAsyncSupportUtils = {
  getAsyncMethodStub: (jsInterfaceName, methodName) => {
    return (...args) => new Promise((resolve, reject) => {
      let callbackId = uuid.v4()
      jsInterfaceAsyncMethodCallbackUtils.addCallback(callbackId, resolve, reject)
      //noinspection JSUnresolvedReference
      window['android_AsyncTaskJsInterface'].invokeAsyncMethod(jsInterfaceName, methodName, callbackId, JSON.stringify(args))
    })
  }
}

export default jsInterfaceAsyncSupportUtils