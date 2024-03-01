import codeUtils from '@/utils/code'

class InertialScrollEngine {

  #movingY

  #speedCalcTask

  #lastMovingYOfSpeedCalc

  #speedArray = []

  #runningScrollTask

  #scrollActionCallback = () => {}

  constructor(scrollActionCallback) {
    this.#scrollActionCallback = scrollActionCallback
  }

  onTouchStart(e) {
    this.stopScroll()
    let startY = e.changedTouches[0].clientY
    this.#movingY = startY
    this.#lastMovingYOfSpeedCalc = startY
    this.#speedArray = []
    this.#speedCalcTask = setInterval(() => {
      let speed = this.#movingY - this.#lastMovingYOfSpeedCalc
      this.#lastMovingYOfSpeedCalc = this.#movingY
      this.#speedArray.push(speed)
    }, 10)
  }

  onTouchMove(e) {
    this.#movingY = e.changedTouches[0].clientY
  }

  onTouchEnd() {
    clearInterval(this.#speedCalcTask)
    let lastSpeeds = this.#speedArray.length <= 10 ? this.#speedArray : this.#speedArray.slice(this.#speedArray.length - 10)
    if(lastSpeeds.length === 0) return
    let speedSum = 0
    lastSpeeds.forEach(it => {
      speedSum += it
    })
    this.#inertialScroll(speedSum / lastSpeeds.length)
  }

  #inertialScroll(speed) {
    let direct = speed >= 0 ? 1 : -1
    let scrollActionsCount = 0
    let absSpeed = Math.abs(speed)
    let waitTime = 4
    if(absSpeed < 0.8) return
    let scroll = () => {
      let distance = (absSpeed < 0.8 ? 0.8 : absSpeed) * direct
      this.#scrollActionCallback(distance)
      scrollActionsCount += 1
      absSpeed = this.#decreaseSpeed(absSpeed)
    }
    let scrollTask = async () => {
      while(absSpeed > 0 && scrollTask === this.#runningScrollTask) {
        scroll()
        await codeUtils.sleep(waitTime)
        if(absSpeed < 0.8 && scrollActionsCount % 4 === 0) waitTime += 1
      }
    }
    this.#runningScrollTask = scrollTask
    setTimeout(scrollTask)
  }

  #decreaseSpeed(speed) {
    if(speed > 50) return speed - 0.5
    if(speed > 20) return speed - 0.35
    if(speed > 10) return speed - 0.2
    if(speed > 3) return speed - 0.05
    return speed - 0.02
  }

  stopScroll() {
    this.#runningScrollTask = null
  }
}

export default InertialScrollEngine