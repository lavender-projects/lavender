import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import request from '@/utils/request'

const methodDefinitions = {
  getLocalLavsourceListCanBeAdded: {
    isAsync: true,
    fallback: () => request({
      url: '/lavsource/localLavsourceListCanBeAdded',
      method: 'get'
    })
  },
  addLocalLavsource: {
    isAsync: true,
    fallback: data => request({
      url: '/lavsource/addLocalLavsource',
      method: 'post',
      data
    })
  },
  getExistingLavsourceList: {
    isAsync: true,
    fallback: () => request({
      url: '/lavsource/existingLavsourceList',
      method: 'get'
    })
  }
}

const lavsourceJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceJsInterface