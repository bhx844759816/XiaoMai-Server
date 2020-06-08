package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.DeviceDetailModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.utils.ShellUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.telnet.TelnetClient

/**
 * 设备列表的ViewModel
 */
class DeviceListViewModel : BaseViewModel() {
    private val mHomeRepository = InjectorUtils.getHomeRepository()
    val operateDeviceObserver = MutableLiveData<Boolean>() //添加设备结果
    val deviceDetailListObserver = MutableLiveData<List<DeviceDetailModel>>()//获取设备列表
    private val mTelnetClient by lazy {
        TelnetClient().apply {
            soTimeout = 2000
        }
    }

    /**
     * 根据学校ID获取设备列表
     */
    fun getDeviceList(schoolId: String) {
        launchUI {
            try {
                val result = withContext(Dispatchers.IO) {
                    mHomeRepository.getDeviceList(schoolId)
                }
                if (result.status == 200) {
                    deviceDetailListObserver.postValue(result.data)
                } else {
                    defUI.toastEvent.postValue(result.message)
                }

            } catch (t: Throwable) {
                t.printStackTrace()
                defUI.toastEvent.postValue("网络错误请稍后再试")
            }
        }
    }

    /**
     * 删除设备
     */
    fun deleteDevice(deviceId: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mHomeRepository.deleteDevice(deviceId)
                }
                if (result.status == 200) {
                    operateDeviceObserver.postValue(true)
                } else {
                    defUI.toastEvent.postValue(result.message)
                    operateDeviceObserver.postValue(false)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                defUI.toastEvent.postValue("网络错误请稍后再试")
                operateDeviceObserver.postValue(false)
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }

    fun addDevice(
        deviceId: String?,
        schoolId: String,
        deviceName: String,
        deviceSite: String,
        isTelnet: Boolean,
        ip: String?,
        port: String?
    ) {
        val params = mutableMapOf(
            "schoolId" to schoolId,
            "name" to deviceName,
            "internetAddress" to deviceSite,
            "isTelnet" to if (isTelnet) "1" else "0"
        )
        if (deviceId != null) {
            params["id"] = deviceId
        }
        if (ip?.isNotEmpty() == true) {
            params["ip"] = ip
        }
        if (port?.isNotEmpty() == true) {
            params["port"] = port
        }
        launchUI {
            try {
                LogUtils.i(params.toString())
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mHomeRepository.addDevice(params)
                }
                if (result.status == 200) {//请求成功
                    operateDeviceObserver.postValue(true)
                } else {
                    defUI.toastEvent.postValue(result.message)
                    operateDeviceObserver.postValue(false)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                defUI.toastEvent.postValue("网络错误请稍后再试")
                operateDeviceObserver.postValue(false)
            } finally {
                defUI.dismissDialog.call()
            }
        }
    }

    /**
     * 做telnet检测
     */
    private suspend fun doTelnetCheck(ip: String, port: Int): Boolean {
        try {
            return withContext(Dispatchers.IO) {
                mTelnetClient.connect(ip, port)
                true
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 执行ping命令检测
     */
    private suspend fun doPingCheck(ip: String): Boolean {
        try {
            return withContext(Dispatchers.IO) {
                val result = ShellUtils.execCmd("ping -c 3 $ip", false)
                LogUtils.i("errorMsg = ${result.errorMsg}")
                LogUtils.i("success = ${result.successMsg}")
                result.result == 0
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return false
    }


}