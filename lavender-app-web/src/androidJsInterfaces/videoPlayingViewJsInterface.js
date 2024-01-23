import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const methodDefinitions = {
  simulateClickBeforeVideoPlay: jsInterfaceUtils.emptyImplementation()
}

const videoPlayingViewJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'VideoPlayingViewJsInterface', methodDefinitions
) ?? methodDefinitions

export default videoPlayingViewJsInterface