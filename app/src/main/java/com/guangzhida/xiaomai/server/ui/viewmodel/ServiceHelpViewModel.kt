package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.ServiceHelpItemModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * 客服校园网凌风计费的帮助ViewModel
 */
class ServiceHelpViewModel : BaseViewModel() {
    val mContentListObserver = MutableLiveData<List<Pair<String, List<ServiceHelpItemModel>>>>()
    private val mHomeRepository = InjectorUtils.getHomeRepository()

    /**
     * 获取所有可操作的列表
     */
    fun getServiceHelpItemList(url: String, params: Map<String, String>) {
        launchUI {
            try {
                val contentList = mutableListOf<Pair<String, List<ServiceHelpItemModel>>>()
                withContext(Dispatchers.IO) {
                    val responseBody = mHomeRepository.getServiceHelpList(url, params)
                    val result =
                        BufferedReader(InputStreamReader(responseBody.byteStream())).useLines { lines ->
                            val results = StringBuilder()
                            lines.forEach { results.append(it) }
                            results.toString()
                        }
                    if (result.isNotEmpty()) {
                        LogUtils.i("result=$result")
                        val jsonArray = JSONArray(result)
                        val gson = Gson()
                        for (i in 0 until jsonArray.length()) {
                            val content = jsonArray.getString(i)
                            LogUtils.i("content=$content")
                            val list =
                                gson.fromJson<List<ServiceHelpItemModel>>(
                                    content,
                                    object : TypeToken<List<ServiceHelpItemModel>>() {}.type
                                )
                            LogUtils.i("list=$list")
                            contentList.add(Pair(list[0].title, list))
                        }
                    }

                }
                mContentListObserver.postValue(contentList)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }


}