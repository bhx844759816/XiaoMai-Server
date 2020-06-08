package com.guangzhida.xiaomai.server.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.guangzhida.xiaomai.ktxlibrary.ext.isPhone
import com.guangzhida.xiaomai.ktxlibrary.ext.telephonyManager
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.model.SchoolCardModel
import com.guangzhida.xiaomai.server.ui.adapter.SchoolCardListAdapter
import com.guangzhida.xiaomai.server.ui.viewmodel.SchoolCardListViewModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_school_card_list_layout.*
import permissions.dispatcher.ktx.withPermissionsCheck
import java.security.Permission


/**
 * 办理校园卡List的页面
 */
class SchoolCardListActivity : BaseActivity<SchoolCardListViewModel>() {
    private val mSchoolCardList = mutableListOf<SchoolCardModel>()
    private val mAdapter by lazy {
        SchoolCardListAdapter(mSchoolCardList)
    }


    override fun getLayoutId(): Int = R.layout.activity_school_card_list_layout

    override fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        mAdapter.loadMoreModule!!.setOnLoadMoreListener {
            mViewModel.getSchoolCardList(false, BaseApplication.instance().mUserModel!!.id)
        }
        mAdapter.loadMoreModule!!.isAutoLoadMore = true
        //当自动加载开启，同时数据不满一屏时，是否继续执行自动加载更多(默认为true)
        mAdapter.loadMoreModule!!.isEnableLoadMoreIfNotFullPage = false
        //获取
        mViewModel.getSchoolCardList(true, BaseApplication.instance().mUserModel!!.id)

    }

    override fun initListener() {
        toolBar.setNavigationOnClickListener {
            finish()
        }
        //刷新
        swipeRefresh.setOnRefreshListener {
            mAdapter.loadMoreModule?.isEnableLoadMore = false
            mViewModel.getSchoolCardList(true, BaseApplication.instance().mUserModel!!.id)
        }
        mAdapter.mOperateCallBack = { status, item ->
            mViewModel.doOperateSchoolCardStatus(item, status)
        }
        mAdapter.mCallPhoneCallBack = {
            if (it.isPhone()) {
                callPhone(it)
            } else {
                ToastUtils.toastShort("电话号码不合法")
            }
        }
    }

    private fun callPhone(phone: String) =
        withPermissionsCheck(Manifest.permission.CALL_PHONE, onShowRationale = {
            it.proceed()
        }) {
            val intent = Intent(Intent.ACTION_CALL)
            val data = Uri.parse("tel:$phone")
            intent.setData(data)
            startActivity(intent)
        }

    override fun initObserver() {
        mViewModel.mLoadMoreResultObserver.observe(this, Observer {
            when (it) {
                0 -> { //加载完成
                    mAdapter.loadMoreModule?.loadMoreComplete();
                }
                1 -> {
                    mAdapter.loadMoreModule?.loadMoreFail();

                }
                2 -> {
                    mAdapter.loadMoreModule?.loadMoreEnd();
                }
            }
        })
        mViewModel.mRefreshResultObserver.observe(this, Observer {
            swipeRefresh.isRefreshing = false
        })
        mViewModel.mDataResultObserver.observe(this, Observer {
            val list = it.second
            if (it.first) {
                mAdapter.loadMoreModule?.isEnableLoadMore = true
                mSchoolCardList.clear()
                mSchoolCardList.addAll(list)
            } else {
                mSchoolCardList.addAll(list)
            }
            mAdapter.notifyDataSetChanged()
        })

        mViewModel.mSchoolCardUpdateSuccessObserver.observe(this, Observer {
            if (it) {
                mAdapter.notifyDataSetChanged()
            }
        })
    }
}