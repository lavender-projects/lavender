import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import request from '@/utils/request'

const methodDefinitions = {
  getLocalLavsourceListCanBeAdded: {
    isAsync: true,
    fallback: () => request({
      url: '/lavsource/localLavsourceListCanBeAdded',
      method: 'get'
    })
  }
}

const lavsourceJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceJsInterface