import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const methodDefinitions = {
  openNewWebActivity: path => {
    window.location.href = path
  },
  finishCurrentWebActivity: () => {
    history.back()
  }
}

const basicJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'BasicJsInterface', methodDefinitions
) ?? methodDefinitions

export default basicJsInterface