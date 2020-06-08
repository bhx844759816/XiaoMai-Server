package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 登录的ViewModel
 */
class LoginViewModel : BaseViewModel() {

    private val mRepository = InjectorUtils.getLoginRepository()
    val mLoginResult = MutableLiveData<String>()
    val mChatLoginResult = MutableLiveData<Boolean>()

    /**
     * 执行登录
     */
    fun doLogin(phone: String, passWord: String) {
        launchUI {
            defUI.showDialog.call()
            try {
                val result = withContext(Dispatchers.IO) {
                    mRepository.doLogin(phone, passWord)
                }
                if (result.status == 200) {
                    //登录成功
                    val userJson = Gson().toJson(result.data)
                    BaseApplication.instance().mUserModel = result.data
                    mLoginResult.postValue(userJson)
                    doChatLogin(phone, passWord)
                } else {
                    defUI.toastEvent.postValue(result.message)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                mChatLoginResult.postValue(false)
                defUI.toastEvent.postValue("登录失败请稍后再试")
            }
        }
    }

    fun dismissDialog(){
        defUI.dismissDialog.call()
    }

    /**
     * 登录环信
     */
    private fun doChatLogin(phone: String, password: String) {
        try {
            EMClient.getInstance().login(phone, password, object : EMCallBack {
                override fun onSuccess() {
                    //加载全部会话
                    EMClient.getInstance().chatManager().loadAllConversations()
                    defUI.toastEvent.postValue("登录成功")
                    mChatLoginResult.postValue(true)

                }

                override fun onProgress(progress: Int, status: String?) {
                }

                override fun onError(code: Int, error: String?) {
                    defUI.toastEvent.postValue("登录失败:$error")
                    mChatLoginResult.postValue(false)
                }
            })
        }catch (t:Exception){
            t.printStackTrace()
        }

    }
}