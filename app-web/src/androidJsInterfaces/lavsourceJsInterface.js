import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'
import lavsourceJsInterfaceMockData from '@/mockData/lavsourceJsInterface'

//noinspection JSUnusedLocalSymbols
const methodDefinitions = {
  getLocalLavsourceListCanBeAdded: {
    isAsync: true,
    fallback: async () => lavsourceJsInterfaceMockData.getLocalLavsourceListCanBeAdded
  },
  addLocalLavsource: {
    isAsync: true,
    fallback: async data => null
  },
  getExistingLavsourceList: {
    isAsync: true,
    fallback: () => lavsourceJsInterfaceMockData.getExistingLavsourceList
  },
  getLavsourceStatus: {
    isAsync: true,
    fallback: async id => true
  }
}

const lavsourceJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceJsInterface