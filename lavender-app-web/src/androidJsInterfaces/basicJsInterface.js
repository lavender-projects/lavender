import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const methodDefinitions = {
  openNewWebActivity: path => {
    jsInterfaceUtils.jsInterfaceWarning()
    window.location.href = path
  },
  finishCurrentWebActivity: () => {
    jsInterfaceUtils.jsInterfaceWarning()
    history.back()
  }
}

const basicJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'BasicJsInterface', methodDefinitions
) ?? methodDefinitions

export default basicJsInterface