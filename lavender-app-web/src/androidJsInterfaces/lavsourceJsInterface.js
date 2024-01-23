import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import request from '@/utils/request'

const methodDefinitions = {
  getLavsourceListCanBeAdded: {
    isAsync: true,
    fallback: () => request({
      url: '/lavsource/lavsourceListCanBeAdded',
      method: 'get'
    })
  }
}

const lavsourceJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceJsInterface