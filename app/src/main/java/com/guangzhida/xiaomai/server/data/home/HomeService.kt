package com.guangzhida.xiaomai.server.data.home

import com.guangzhida.xiaomai.server.DEFAULT_CUR_PAGE
import com.guangzhida.xiaomai.server.DEFAULT_PAGE_SIZE
import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.model.*
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 校园网模块的接口
 */
interface HomeService {

    /**
     * 获取校园卡办理的列表
     * @param pageSize 分页请求的大小 默认20
     * @param curPage 当前请求的页数
     * @param serviceId 网管的ID
     */
    @FormUrlEncoded
    @POST("school/schoolcard/getSchoolCardByCustomerPage")
    suspend fun getSchoolCardDataList(
        @Field("limit") pageSize: Int,
        @Field("page") curPage: Int,
        @Field("customerServiceId") serviceId: String
    ): BaseResult<PageDataModel<SchoolCardModel>>

    /**
     * 获取所有的学校
     */
    @POST("schoolinfo/get_list")
    suspend fun getSchoolList(): BaseResult<List<SchoolModel>>


    /**
     * 添加学校
     * @param schoolName 学校名称
     * @param managerId 网管ID
     */
    @FormUrlEncoded
    @POST("network/school_info/add_school_info")
    suspend fun addSchoolInfo(@Field("schoolName") schoolName: String, @Field("managerId") managerId: String): BaseResult<String>

    /**
     * 获取学校列表
     */
    @FormUrlEncoded
    @POST("network/school_info/getSchoolListByManagerId")
    suspend fun getSchoolList(@Field("managerId") managerId: String): BaseResult<List<SchoolDetailModel>>

    /**
     *删除学校通过学校ID
     **/
    @FormUrlEncoded
    @POST("network/school_info/deleteSchool")
    suspend fun deleteSchool(@Field("schoolId") schoolId: String): BaseResult<String>

    /**
     * 修改学校信息
     */
    @FormUrlEncoded
    @POST("network/school_info/updateSchoolName")
    suspend fun updateSchool(@Field("schoolId") schoolId: String, @Field("schoolName") schoolName: String): BaseResult<String>


    /**
     * 根据学校信息获取设备列表
     */
    @FormUrlEncoded
    @POST("network/school_info/getEquipmentsBySchoolId")
    suspend fun getDeviceList(@Field("schoolId") schoolId: String): BaseResult<List<DeviceDetailModel>>

    /**
     * 添加设备
     */
    @FormUrlEncoded
    @POST("network/school_info/add_school_equipment")
    suspend fun addDevice(@FieldMap params: Map<String, String>): BaseResult<String>

    /**
     * 删除设备
     */
    @FormUrlEncoded
    @POST("network/school_info/deleteEquipment")
    suspend fun deleteDevice(@Field("id") deviceId: String): BaseResult<String>

    /**
     * 清空用户套餐
     */
    @FormUrlEncoded
    @POST("http/costClearing")
    suspend fun clearAccountPackage(@Field("user") user: String): BaseResult<String>

    /**
     * 更新校园卡办理状态
     * @param status 状态 0未处理 1已处理 2处理失败
     */
    @FormUrlEncoded
    @POST("school/schoolcard/updateStatus")
    suspend fun updateSchoolCardStatus(@Field("id") id: String, @Field("status") status: String): BaseResult<String>

    /**
     * 通过账号和密码获取凌风radius用户下所有可以操作的标签
     */
    @FormUrlEncoded
    @POST("")
    suspend fun getServiceHelpList(@Url url: String, @FieldMap params: Map<String, String>): ResponseBody






}