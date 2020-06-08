package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.SchoolDetailModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 学校列表的ViewModel
 */
class SchoolListViewModel : BaseViewModel() {
    private val mRepository = InjectorUtils.getHomeRepository()
    val schoolListObserver = MutableLiveData<List<SchoolDetailModel>>() //学校列表
    val addSchoolObserver = MutableLiveData<Boolean>() //添加学校
    val modifySchoolMessageObserver = MutableLiveData<Boolean>() //修改学校
    val deleteSchoolObserver = MutableLiveData<Boolean>() //删除学校


    /**
     * 获取学校列表通过网管的ID
     */
    fun getSchoolList(managerId: String) {
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
            }
        }
    }

    /**
     * 添加学校
     */
    fun addSchoolInfo(managerID: String, schoolName: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mRepository.addSchoolInfo(schoolName, managerID)
                }
                if (result.status == 200) {
                    addSchoolObserver.postValue(true)
                } else {
                    addSchoolObserver.postValue(false)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                addSchoolObserver.postValue(false)
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }


    fun deleteSchool(schoolId: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mRepository.deleteSchool(schoolId)
                }
                if (result.status == 200) {
                    deleteSchoolObserver.postValue(true)
                } else {
                    deleteSchoolObserver.postValue(false)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                deleteSchoolObserver.postValue(false)
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }

    fun updateSchool(schoolId: String, schoolName: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mRepository.updateSchool(schoolId, schoolName)
                }
                if (result.status == 200) {
                    modifySchoolMessageObserver.postValue(true)
                } else {
                    modifySchoolMessageObserver.postValue(false)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                modifySchoolMessageObserver.postValue(false)
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }
}