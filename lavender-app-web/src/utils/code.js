import { showConfirmDialog } from 'vant'

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
  showConfirmDialog({ title, message, onConfirm }) {
    showConfirmDialog({
      title,
      message,
      beforeClose: async action => {
        if(action !== 'confirm') return true
        try {
          await onConfirm()
        } catch(e) {
          console.log(e)
        }
        //返回true表示应该关闭弹窗，而不是beforeClose的预定义行为执行成功
        return true
      }
    }).then(() => {
      //仅当action为confirm时，才执行then中的内容
    }).catch(() => {
      //当action不为confirm时，执行catch中的内容。若不调用catch指定回调，则控制台将会输出Promise Error信息
    })
  }
}

export default codeUtils