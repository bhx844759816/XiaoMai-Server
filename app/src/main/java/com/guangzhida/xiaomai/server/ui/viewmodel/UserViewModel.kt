package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.UserModel
import com.guangzhida.xiaomai.server.room.AppDataBase
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.hyphenate.chat.EMClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel : BaseViewModel() {
    val mExitLoginFinish = MutableLiveData<Boolean>()
    val mUserModelResult = MutableLiveData<Boolean>()
    val mChangeOnLineStatusResult = MutableLiveData<Int>() //改变上线下线
    val mModifyUserInfoResult = MutableLiveData<String>()
    private val mRepository = InjectorUtils.getUserRepository()

    private val mConversationDao by lazy {
        AppDataBase.invoke(BaseApplication.instance().applicationContext).conversationDao()
    }

    fun exitLogin() {
        launchUI {
            try {
                withContext(Dispatchers.IO) {
                    EMClient.getInstance().logout(true)
                    val queryAll = mConversationDao?.queryAll()
                    queryAll?.forEach {
                        mConversationDao?.delete(it)
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                mExitLoginFinish.postValue(true)
            }
        }
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo() {
        if (BaseApplication.instance().mUserModel == null) {
            return
        }
        launchUI {
            try {
                val result = withContext(Dispatchers.IO) {
                    mRepository.getUserInfo(BaseApplication.instance().mUserModel!!.id)
                }
                if (result.status == 200) {
                    val userJson = Gson().toJson(result.data)
                    BaseApplication.instance().mUserModel = result.data
                    mModifyUserInfoResult.postValue(userJson)
                }
            } catch (t: Throwable) {
                t.printStackTrace()

            }finally {
                mUserModelResult.postValue(true)
            }
        }
    }

    /**
     * 改变上线下线
     */
    fun changeOnlineStatus() {
        launchUI {
            try {
                val result = withContext(Dispatchers.IO) {
                    mRepository.operateServerOnLineStatus(BaseApplication.instance().mUserModel!!.id)
                }
                if (result.status == 200) {
                    val userJson = Gson().toJson(result.data)
                    BaseApplication.instance().mUserModel = result.data
                    mModifyUserInfoResult.postValue(userJson)
                    mChangeOnLineStatusResult.postValue(result.data?.isOnline)
                }

            } catch (t: Throwable) {
                t.printStackTrace()
            }finally {
                mUserModelResult.postValue(true)
            }
        }
    }
}