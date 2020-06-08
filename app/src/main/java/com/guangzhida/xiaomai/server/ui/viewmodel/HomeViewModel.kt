package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.SchoolDetailModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {
    private val mRepository = InjectorUtils.getHomeRepository()
    val schoolListObserver = MutableLiveData<List<SchoolDetailModel>>() //学校列表
    val refreshObserver = MutableLiveData<Boolean>() //学校列表
    /**
     * 获取全部的学校
     */
    fun getAllSchoolInfo(managerId: String) {
        launchUI {
            try {
                val result = withContext(Dispatchers.IO) {
                    mRepository.getSchoolList(managerId)
                }
                if (result.status == 200) {
                    schoolListObserver.postValue(result.data)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                refreshObserver.postValue(true)
            }
        }
    }

    fun clearPackage(phone: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mRepository.clearAccountPackage(phone)
                }
                if (result.status == 200) {
                    defUI.toastEvent.postValue("清除套餐成功")
                } else {
                    defUI.toastEvent.postValue(result.message)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                defUI.toastEvent.postValue("网络错误请稍后重试")
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }
}