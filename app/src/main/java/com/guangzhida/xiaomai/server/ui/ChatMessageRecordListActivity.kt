package com.guangzhida.xiaomai.server.ui

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.hideKeyboard
import com.guangzhida.xiaomai.ktxlibrary.ext.listener.textWatcher
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.model.ChatMessageRecordModel
import com.guangzhida.xiaomai.server.ui.adapter.ChatMessageRecordAdapter
import com.guangzhida.xiaomai.server.ui.viewmodel.ChatMessageRecordListViewModel
import kotlinx.android.synthetic.main.activity_chat_message_record_list_layout.*
import java.io.Serializable

class ChatMessageRecordListActivity : BaseActivity<ChatMessageRecordListViewModel>() {
    private val mChatMessageRecordModelList = mutableListOf<ChatMessageRecordModel>()
    private val mAdapter by lazy {
        ChatMessageRecordAdapter(mChatMessageRecordModelList)
    }
    private var mSearchKey: String = ""
    private var mUserName: String = ""
    private var mNickName: String = ""

    override fun getLayoutId(): Int = R.layout.activity_chat_message_record_list_layout


    override fun initView() {
        mSearchKey = intent.getStringExtra("SearchKey")
        mUserName = intent.getStringExtra("UserName")
        mNickName = intent.getStringExtra("NickName")
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        etInput.textWatcher {
            afterTextChanged {
                mSearchKey = it?.toString() ?: ""
                if (mSearchKey.isNotEmpty()) {
                    mViewModel.queryChatMessageRecord(mSearchKey, mUserName)
                }
            }
        }
        etInput.setText(mSearchKey)
        etInput.setSelection(mSearchKey.length)
    }

    override fun initListener() {

        //点击指定的聊天Item
        mAdapter.mContentClickCallBack = {
            mViewModel.queryChatMessageByMsgId(it.atTime, mUserName)
        }
        tvCancel.clickN {
            hideKeyboard()
            finish()
        }
    }

    override fun initObserver() {
        //获取搜索结果
        mViewModel.mChatMessageRecordModelList.observe(this, Observer {
            val sortList = it.sortedBy { model ->
                model.atTime
            }.asReversed()
            mChatMessageRecordModelList.clear()
            mChatMessageRecordModelList.addAll(sortList)
            mAdapter.notifyDataSetChanged()
        })
        //获取到此条消息后所有的消息
        mViewModel.mQueryChatMessageRecord.observe(this, Observer {
            startKtxActivity<ChatActivity>(
                values = listOf(
                    Pair("userName", mUserName),
                    Pair("EMMessageList", it as Serializable),
                    Pair("State", 1)
                )
            )
        })
    }
}