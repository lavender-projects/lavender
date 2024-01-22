import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const basicJsInterface = jsInterfaceUtils.getJsInterfaceStub('BasicJsInterface', {
  openNewWebActivity: path => {
    jsInterfaceUtils.jsInterfaceWarning()
    window.location.href = path
  },
  finishCurrentWebActivity: () => {
    jsInterfaceUtils.jsInterfaceWarning()
    history.back()
  }
})

export default basicJsInterface