import codeUtils from '@/utils/code'
import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const basicJsInterface = jsInterfaceUtils.getJsInterfaceStub('BasicJsInterface', {
  openNewWebActivity: path => {
    jsInterfaceUtils.jsInterfaceWarning()
    window.location.href = path
  },
  finishCurrentWebActivity: () => {
    jsInterfaceUtils.jsInterfaceWarning()
    history.back()
  },
  getUuid: () => codeUtils.randomUUID()
})

export default basicJsInterface