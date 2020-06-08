package com.guangzhida.xiaomai.server.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.guangzhida.xiaomai.server.DEFAULT_CUR_PAGE
import com.guangzhida.xiaomai.server.base.BaseViewModel
import com.guangzhida.xiaomai.server.data.InjectorUtils
import com.guangzhida.xiaomai.server.model.SchoolCardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 添加校园卡的列表页面
 */
class SchoolCardListViewModel : BaseViewModel() {
    private val mHomeRepository = InjectorUtils.getHomeRepository()
    private var curPage = DEFAULT_CUR_PAGE
    private val pageSize = DEFAULT_BUFFER_SIZE

    val mRefreshResultObserver = MutableLiveData<Boolean>()
    val mLoadMoreResultObserver = MutableLiveData<Int>() //0 请求成功 1 请求失败 2 没有更多数据了
    val mSchoolCardUpdateSuccessObserver = MutableLiveData<Boolean>() //0 请求成功 1 请求失败 2 没有更多数据了
    val mDataResultObserver = MutableLiveData<Pair<Boolean, List<SchoolCardModel>>>()

    /**
     * 获取办理校园卡的列表
     */
    fun getSchoolCardList(isRefresh: Boolean, serviceId: String) {
        launchUI {
            try {
                if (isRefresh) {
                    curPage = DEFAULT_CUR_PAGE
                }
                val result = withContext(Dispatchers.IO) {
                    mHomeRepository.getSchoolCardDataList(pageSize, curPage, serviceId)
                }
                if (result.status == 200) {
                    result.data?.rows?.let {
                        curPage++
                        mDataResultObserver.postValue(Pair(isRefresh, it))
                        //没有更多数据了
                        if (!isRefresh) {
                            if (it.isEmpty()) {
                                mLoadMoreResultObserver.postValue(2)
                            } else {
                                mLoadMoreResultObserver.postValue(0)
                            }
                        }
                    }
                } else {
                    if (!isRefresh) {
                        mLoadMoreResultObserver.postValue(1)
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                if (!isRefresh) {
                    mLoadMoreResultObserver.postValue(1)
                }
            } finally {
                if (isRefresh) {
                    mRefreshResultObserver.postValue(true)
                }
            }
        }
    }

    /**
     * 操作校园卡办理的状态
     */
    fun doOperateSchoolCardStatus(item: SchoolCardModel, status: String) {
        launchUI {
            try {
                defUI.showDialog.call()
                val result = withContext(Dispatchers.IO) {
                    mHomeRepository.updateSchoolCardStatus(item.iid, status)
                }
                if (result.status == 200) {
                    item.status = status
                    mSchoolCardUpdateSuccessObserver.postValue(true)
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