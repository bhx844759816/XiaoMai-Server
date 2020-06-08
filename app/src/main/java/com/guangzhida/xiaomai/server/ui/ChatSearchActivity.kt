package com.guangzhida.xiaomai.server.ui

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.hideKeyboard
import com.guangzhida.xiaomai.ktxlibrary.ext.listener.textWatcher
import com.guangzhida.xiaomai.ktxlibrary.ext.showKeyboard
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.model.SearchMessageModel
import com.guangzhida.xiaomai.server.ui.adapter.SearchMessageListAdapter
import com.guangzhida.xiaomai.server.ui.viewmodel.ChatSearchViewModel
import kotlinx.android.synthetic.main.activity_chat_search_layout.*

/**
 * 搜索聊天记录
 */
class ChatSearchActivity : BaseActivity<ChatSearchViewModel>() {
    private var mSearchKey: String = ""
    private val mSearchMessageModelList = mutableListOf<SearchMessageModel>()
    private val mAdapter by lazy {
        SearchMessageListAdapter(mSearchMessageModelList)
    }

    override fun getLayoutId(): Int = R.layout.activity_chat_search_layout

    override fun initView() {
        showKeyboard(etInput)
        rvChatMessage.layoutManager = LinearLayoutManager(this)
        rvChatMessage.adapter = mAdapter
    }

    override fun initListener() {
        etInput.textWatcher {
            afterTextChanged {
                mSearchKey = it?.toString() ?: ""
                if (mSearchKey.isNotEmpty()) {
                    mViewModel.doSearch(mSearchKey)
                }
            }
        }
        mAdapter.mContentClickCallBack = {
            val params = listOf(
                Pair("SearchKey", mSearchKey),
                Pair("UserName", it.entity.userName),
                Pair("NickName", it.entity.nickName)
            )
            startKtxActivity<ChatMessageRecordListActivity>(values = params)
        }
        tvCancel.clickN {
            hideKeyboard()
            finish()
        }
    }

    override fun initObserver() {
        mViewModel.searchChatMessageMap.observe(this, Observer {
            val list = it.entries.map { map ->
                map.value
            }
            mSearchMessageModelList.clear()
            mSearchMessageModelList.addAll(list)
            mAdapter.notifyDataSetChanged()
        })

    }
}