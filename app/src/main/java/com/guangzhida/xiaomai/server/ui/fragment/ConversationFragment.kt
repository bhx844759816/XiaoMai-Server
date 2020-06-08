package com.guangzhida.xiaomai.server.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseFragment
import com.guangzhida.xiaomai.server.chat.ChatHelper
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey
import com.guangzhida.xiaomai.server.model.ConversationModelWrap
import com.guangzhida.xiaomai.server.ui.ChatActivity
import com.guangzhida.xiaomai.server.ui.ChatSearchActivity
import com.guangzhida.xiaomai.server.ui.adapter.ConversationAdapter
import com.guangzhida.xiaomai.server.ui.viewmodel.ConversationViewModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_conversation.*

/**
 * 互动模块
 */
class ConversationFragment : BaseFragment<ConversationViewModel>() {
    private val mList = mutableListOf<ConversationModelWrap>()
    private val mAdapter by lazy { ConversationAdapter(mList) }

    override fun layoutId(): Int = R.layout.fragment_conversation

    /**
     * 每次接收到消息得时候开启任务去后台拿取用户信息
     *
     * 每次进入页面首先开启后台任务拉取用户信息
     *
     */
    private val mMessageListener = object : EMMessageListener {
        override fun onMessageRecalled(messages: MutableList<EMMessage>?) {

        }

        override fun onMessageChanged(message: EMMessage?, change: Any?) {
        }

        override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {

        }

        override fun onMessageReceived(messages: MutableList<EMMessage>) {
            viewModel.loadAllConversation()
        }

        override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
        }

        override fun onMessageRead(messages: MutableList<EMMessage>?) {
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        swipeRefresh.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        mAdapter.animationEnable = true
        recyclerView.adapter = mAdapter

    }

    override fun initListener() {
        //下拉刷新
        swipeRefresh.setOnRefreshListener {
            viewModel.loadAllConversation()
        }
        mAdapter.mClickContentCallBack = {
            startKtxActivity<ChatActivity>(
                values = listOf(
                    Pair(
                        "userName",
                        it.conversationEntity?.userName ?: ""
                    ), Pair(
                        "nickName",
                        it.conversationEntity?.nickName ?: ""
                    ), Pair(
                        "userAvatar",
                        it.conversationEntity?.avatarUrl ?: ""
                    )
                )
            )
        }
        //长按弹出对话框
        mAdapter.mLongClickContentCallBack = { item, view ->
            showPopupMenu(item, view)
        }
        clChatSearchParent.clickN {
            startKtxActivity<ChatSearchActivity>()
        }
    }

    override fun lazyLoadData() {

    }

    override fun initObserver() {
        //所有的会话列表
        viewModel.mConversationListLiveData.observe(this, Observer {
            mList.clear()
            mList.addAll(it)
            mAdapter.notifyDataSetChanged()
        })
        //下拉刷新的监听
        viewModel.mSwipeRefreshLiveData.observe(this, Observer {
            swipeRefresh.isRefreshing = false
        })
        //删除会话的回调监听
        viewModel.deleteConversationResult.observe(this, Observer {
            if (it != null) {
                mList.remove(it)
                mAdapter.notifyDataSetChanged()
            }
        })
        //置顶会话的回调监听
        viewModel.topConversationResult.observe(this, Observer {
            if (it) {
                viewModel.loadAllConversation()
            }
        })
        LiveDataBus.with(LiveDataBusKey.LOGIN_KEY, Boolean::class.java).observe(this, Observer {
            mList.clear()
            mAdapter.notifyDataSetChanged()
            viewModel.loadAllConversation()
        })
        LiveDataBus.with(LiveDataBusKey.EXIT_LOGIN_KEY, Boolean::class.java)
            .observe(this, Observer {
                mList.clear()
                mAdapter.notifyDataSetChanged()
            })
    }

    override fun onResume() {
        super.onResume()
        //清除通知栏
        ChatHelper.cancelNotifyMessage()
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener)
        viewModel.loadAllConversation()
    }

    override fun onPause() {
        super.onPause()
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener)
    }


    /**
     * 展示长按的PopupMenu
     */
    private fun showPopupMenu(item: ConversationModelWrap, view: View) {
        XPopup.Builder(context)
            .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
            .hasShadowBg(false)
            .isCenterHorizontal(true)
            .asAttachList(getPopupArrayList(item), null) { position, _ ->
                when (position) {
                    0 -> { //置顶 或取消置顶
                        viewModel.makeConversationTop(item)
                    }
                    1 -> {//删除该聊天
                        viewModel.deleteConversation(item)
                    }
                }
            }
            .show()
    }

    private fun getPopupArrayList(item: ConversationModelWrap): Array<String> {
        return if (item.conversationEntity?.isTop == true) {
            arrayOf("取消置顶", "删除该聊天")
        } else {
            arrayOf("置顶", "删除该聊天")
        }
    }
}