package com.guangzhida.xiaomai.server.data.home

import com.guangzhida.xiaomai.server.DEFAULT_CUR_PAGE
import com.guangzhida.xiaomai.server.DEFAULT_PAGE_SIZE
import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.http.RetrofitManager
import com.guangzhida.xiaomai.server.model.*
import okhttp3.ResponseBody

/**
 *
 */
class HomeRepository {
    val mService = RetrofitManager.getInstance().create(HomeService::class.java)
    /**
     * 添加学校
     */
    suspend fun addSchoolInfo(schoolName: String, managerId: String): BaseResult<String> {
        return mService.addSchoolInfo(schoolName, managerId)
    }

    /**
     * 获取添加的学校
     */
    suspend fun getSchoolList(managerId: String): BaseResult<List<SchoolDetailModel>> {
        return mService.getSchoolList(managerId)
    }

    /**
     * 删除学校
     */
    suspend fun deleteSchool(schoolId: String): BaseResult<String> {
        return mService.deleteSchool(schoolId)
    }

    /**
     * 删除学校
     */
    suspend fun updateSchool(schoolId: String, schoolName: String): BaseResult<String> {
        return mService.updateSchool(schoolId, schoolName)
    }

    /**
     * 获取设备列表
     */
    suspend fun getDeviceList(schoolId: String): BaseResult<List<DeviceDetailModel>> {
        return mService.getDeviceList(schoolId)
    }

    /**
     * 添加设备
     */
    suspend fun addDevice(params: Map<String, String>): BaseResult<String> {
        return mService.addDevice(params)
    }

    /**
     * 删除设备
     */
    suspend fun deleteDevice(deviceId: String): BaseResult<String> {
        return mService.deleteDevice(deviceId)
    }

    /**
     * 清空用户套餐
     */
    suspend fun clearAccountPackage(user: String): BaseResult<String> {
        return mService.clearAccountPackage(user)
    }

    /**
     * 更新校园卡办理的状态
     */
    suspend fun updateSchoolCardStatus(id: String, status: String): BaseResult<String> {
        return mService.updateSchoolCardStatus(id, status)
    }

    /**
     * 获取凌风radius中所有可操作标签对象
     */
    suspend fun getServiceHelpList(url: String, params: Map<String, String>): ResponseBody {
        return mService.getServiceHelpList(url, params)
    }



    /**
     * 获取所有学校
     */
    suspend fun getSchoolList(): BaseResult<List<SchoolModel>> {
        return mService.getSchoolList()
    }

    /**
     * 获取校园卡办理的数据
     */
    suspend fun getSchoolCardDataList(
        pageSize: Int = DEFAULT_PAGE_SIZE,
        curPage: Int = DEFAULT_CUR_PAGE,
        serviceId: String
    ): BaseResult<PageDataModel<SchoolCardModel>> = mService.getSchoolCardDataList(pageSize, curPage, serviceId)

    companion object {
        @Volatile
        var INSTANCE: HomeRepository? = null

        fun getInstance(): HomeRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: HomeRepository().also { INSTANCE = it }
            }
    }
}