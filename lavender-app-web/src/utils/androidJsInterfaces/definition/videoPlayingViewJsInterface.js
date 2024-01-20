import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const videoPlayingViewJsInterface = jsInterfaceUtils.getJsInterfaceStub('VideoPlayingViewJsInterface', {
  simulateClickBeforeVideoPlay: jsInterfaceUtils.emptyImplementation()
})

export default videoPlayingViewJsInterface