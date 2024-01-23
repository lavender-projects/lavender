import jsInterfaceAsyncSupportUtils from '@/utils/androidJsInterfaces/asyncSupport'

export const jsInterfaceUtils = {
  emptyImplementation: () => () => jsInterfaceUtils.jsInterfaceWarning(),
  jsInterfaceWarning() {
    console.warn('You are calling a Android JavaScript Interface function directly in browser!')
  },
  getJsInterfaceStub(jsInterfaceName, methodDefinitions) {
    let jsInterface = window[`android_${jsInterfaceName}`]
    let stub = {}
    Object.keys(methodDefinitions).forEach(it => {
      let definition = methodDefinitions[it]
      if(definition instanceof Function) {
        stub[it] = jsInterface != null ? this.getWrappedInterfaceMethod(jsInterface, it) : definition
        return
      }
      if(definition instanceof Object) {
        let isAsync = definition.isAsync ?? false
        if(!isAsync) {
          stub[it] = jsInterface != null ? this.getWrappedInterfaceMethod(jsInterface, it) : definition.fallback
        } else {
          stub[it] = jsInterface != null ? jsInterfaceAsyncSupportUtils.getAsyncMethodStub(jsInterfaceName, it) : (
            async (...args) => await definition.fallback(...args)
          )
        }
        return
      }
      throw new Error(`Unknown Android interface stub definition: ${it} -> ${typeof definition}`)
    })
    return stub
  },
  getWrappedInterfaceMethod(jsInterface, methodName) {
    return (...args) => jsInterface[methodName](...args)
  }
}