//noinspection JSUnusedGlobalSymbols
const androidEventListeners = {
  //监听器方法若返回true，表示监听器的预定义行为被触发
  onBackButtonPressedListeners: initListenerGroups(),
  onActivityPauseListeners: initListenerGroups(),
  onActivityResumeListeners: initListenerGroups(),
  //仅在main.js中调用一次
  exposeToGlobal() {
    window.androidEventListeners = this
  },
  executeListeners(name) {
    let listeners = this[name]
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

function initListenerGroups() {
  return {
    videoPlayingView: {}
  }
}

export default androidEventListeners