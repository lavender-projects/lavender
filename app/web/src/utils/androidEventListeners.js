//noinspection JSUnusedGlobalSymbols
export const androidEventListenerUtils = {
  initListenerGroups: () => ({
    videoPlayingView: {}
  }),
  //仅在main.js中调用一次
  exposeToGlobal() {
    window.androidEventListenerUtils = this
  },
  invokeListeners(name) {
    let listeners = androidEventListeners[name]
    if(listeners == null) return
    let result = false
    Object.values(listeners).forEach(listenerGroup => {
      //noinspection JSCheckFunctionSignatures
      Object.values(listenerGroup).forEach(it => {
        let aResult = it()
        if(aResult) result = true
      })
    })
    return result
  }
}

const androidEventListeners = {
  //监听器方法若返回true，表示监听器的预定义行为被触发
  onBackButtonPressedListeners: androidEventListenerUtils.initListenerGroups(),
  onActivityPauseListeners: androidEventListenerUtils.initListenerGroups(),
  onActivityResumeListeners: androidEventListenerUtils.initListenerGroups()
}

export default androidEventListeners