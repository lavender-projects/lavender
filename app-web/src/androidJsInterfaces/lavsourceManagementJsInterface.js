import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import lavsourceManagementJsInterfaceMockData from '@/mockData/lavsourceManagementJsInterface'

//noinspection JSUnusedLocalSymbols
const methodDefinitions = {
  getLocalLavsourceListCanBeAdded: {
    isAsync: true,
    fallback: async () => lavsourceManagementJsInterfaceMockData.getLocalLavsourceListCanBeAdded
  },
  addLocalLavsource: {
    isAsync: true,
    fallback: async data => null
  },
  getExistingLavsourceList: {
    isAsync: true,
    fallback: () => lavsourceManagementJsInterfaceMockData.getExistingLavsourceList
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