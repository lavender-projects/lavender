import { showToast } from 'vant'

const messageUtils = {
  message: str => showToast(str),
  success: str => showToast(str),
  error: str => showToast(str)
}

export default messageUtils