import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const methodDefinitions = {
  simulateClickBeforeVideoPlay: () => {}
}

const videoPlayingViewJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'VideoPlayingViewJsInterface', methodDefinitions
) ?? methodDefinitions

export default videoPlayingViewJsInterface