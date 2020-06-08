package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey
import com.guangzhida.xiaomai.server.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PersonInfoViewModel : BaseViewModel() {
    private val mCommonRepository = InjectorUtils.getCommonRepository()
    private val mUserRepository = InjectorUtils.getUserRepository()

    val mModifyUserInfoResult = MutableLiveData<String>()
    /**
     * 更新用户信息
     */
    fun uploadPersonInfo(file: File? = null, params: MutableMap<String, String>) {
        launchUI {
            try {
                defUI.showDialog.call()
                if (file != null) {
                    val result = withContext(Dispatchers.IO) {
                        mCommonRepository.uploadImg(file)
                    }
                    if (result.status == 200) {
                        val headId = result.message.split(":")[0]
                        val headUrl = result.message.split(":")[1]
                        params["headId"] = headId
                        params["headUrl"] = headUrl
                    }
                }
                //更新用户信息
                val result = withContext(Dispatchers.IO) {
                    mUserRepository.updateUserInfo(params)
                }
                if (result.status == 200) {
                    val userJson = Gson().toJson(result.data)
                    BaseApplication.instance().mUserModel = result.data
                    mModifyUserInfoResult.postValue(userJson)
                    defUI.toastEvent.postValue("更新用户信息成功")
                    //发送用户信息改变的
                    LiveDataBus.with(LiveDataBusKey.USER_INFO_MODIFY, Boolean::class.java)
                        .postValue(true)
                } else {
                    ToastUtils.toastShort(result.message)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                defUI.toastEvent.postValue("网络错误")
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }
}