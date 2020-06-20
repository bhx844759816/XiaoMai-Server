package com.guangzhida.xiaomai.server.ui.viewmodel

import android.preference.Preference
import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.hyphenate.chat.EMClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * 忘记密码
 */
class ForgetPasswordViewModel : BaseViewModel() {
    val mSmsCodeLiveData = MutableLiveData<Boolean>()//发送验证码的数据观察者
    val mConfirmPasswordResultLiveData = MutableLiveData<Boolean>()//注册的数据观察者
    private val loginRepository = InjectorUtils.getLoginRepository()
    /**
     * 发送验证码
     */
    fun sendSmsCode(phone: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    loginRepository.sendSmsCode(phone, "5")
                }
                if (result.status == 200) {
                    mSmsCodeLiveData.postValue(true)
                } else {
                    defUI.toastEvent.postValue(result.message)
                    mSmsCodeLiveData.postValue(false)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }

    /**
     * 确定修改密码
     */
    fun confirmModifyPassword(phone: String, code: String, password: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    loginRepository.modifyPassword(phone, code, password, password)
                }
                if (result.status == 200) {
                    defUI.dismissDialog.call()
                    mConfirmPasswordResultLiveData.postValue(true)
                } else {
                    defUI.toastEvent.postValue(result.message)
                    mConfirmPasswordResultLiveData.postValue(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mConfirmPasswordResultLiveData.postValue(false)
                defUI.dismissDialog.call()
            }
        }
    }


}