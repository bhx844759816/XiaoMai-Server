package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.SchoolModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 注册的ViewModel
 */
class RegisterViewModel : BaseViewModel() {
    private val mRepository = InjectorUtils.getHomeRepository()
    private val mLoginRepository = InjectorUtils.getLoginRepository()
    val mSchoolListObserver = MutableLiveData<List<SchoolModel>>()
    val mSmsSendResultObserver = MutableLiveData<Boolean>()
    val mRegisterResultObserver = MutableLiveData<Boolean>()
    /**
     * 获取所有学校列表
     */
    fun getSchoolList() {
        launchUI {
            try {
                val result = withContext(Dispatchers.IO) {
                    mRepository.getSchoolList()
                }
                if (result.status == 200) {
                    mSchoolListObserver.postValue(result.data)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {


            }
        }
    }

    /**
     * 发送验证码
     */
    fun sendSmsCode(phone: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mLoginRepository.sendSmsCode(phone, type = "3")
                }
                if (result.status == 200) {
                    defUI.toastEvent.postValue("发送验证码成功")
                    mSmsSendResultObserver.postValue(true)
                } else {
                    defUI.toastEvent.postValue("发送验证码失败")
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                defUI.toastEvent.postValue("网络错误请稍后再试")
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }

    /**
     * 注册
     *
     */
    fun register(schoolId: String, phone: String, password: String, code: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mLoginRepository.doRegister(phone, password, code, schoolId)
                }
                if (result.status == 200) {
                    defUI.toastEvent.postValue("注册成功")
                    mRegisterResultObserver.postValue(true)
                } else {
                    defUI.toastEvent.postValue("注册失败，${result.message}")
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                defUI.toastEvent.postValue("网络错误请稍后再试")
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }
}