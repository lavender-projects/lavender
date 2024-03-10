import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import { mockRequest } from '@/utils/request'

//noinspection JSUnusedLocalSymbols
const methodDefinitions = {
  getLocalLavsourceListCanBeAdded: {
    isAsync: true,
    fallback: async () => await mockRequest({
      url: '/lavsourceManagementJsInterface/getLocalLavsourceListCanBeAdded.json',
      method: 'get'
    })
  },
  addLocalLavsource: {
    isAsync: true,
    fallback: async data => null
  },
  getExistingLavsourceList: {
    isAsync: true,
    fallback: async () => await mockRequest({
      url: '/lavsourceManagementJsInterface/getExistingLavsourceList.json',
      method: 'get'
    })
  },
  getLavsourceStatus: {
    isAsync: true,
    fallback: async id => true
  },
  changeLavsourceEnableStatus: {
    isAsync: true,
    fallback: async (id, enabled) => null
  }
}

const lavsourceManagementJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceManagementJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceManagementJsInterface