package com.guangzhida.xiaomai.server.ui

import androidx.lifecycle.Observer
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.getSpValue
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.putSpValue
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.USER_MODEL_KEY
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey
import com.guangzhida.xiaomai.server.ext.loadCircleImage
import com.guangzhida.xiaomai.server.http.BASE_URL
import com.guangzhida.xiaomai.server.ui.viewmodel.UserViewModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import kotlinx.android.synthetic.main.activity_user_layout.*

class UserActivity : BaseActivity<UserViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_user_layout

    override fun initView() {
        mViewModel.getUserInfo()
    }

    override fun initListener() {
        //点击头像
        ivHeaderView.clickN {
            if (BaseApplication.instance().mUserModel == null) {
                startKtxActivity<LoginActivity>()
            } else {
                startKtxActivity<PersonInfoActivity>()
            }
        }
        //点击名称
        tvUserName.clickN {
            if (BaseApplication.instance().mUserModel == null) {
                startKtxActivity<LoginActivity>()
            } else {
                startKtxActivity<PersonInfoActivity>()
            }
        }
        //退出登录
        tvExitLogin.clickN {
            //将本地存储的用户对象置位空
            putSpValue(USER_MODEL_KEY, "")
            BaseApplication.instance().mUserModel = null
            mViewModel.exitLogin()
        }
        rlFinish.clickN {
            finish()
        }
        //个人中心
        rlUserCenter.clickN {
            if (BaseApplication.instance().mUserModel == null) {
                startKtxActivity<LoginActivity>()
            } else {
                startKtxActivity<PersonInfoActivity>()
            }
        }
        //消息设置
        rlMessageSetting.clickN {
            startKtxActivity<MessageSettingActivity>()
        }
        /**
         * 改变上下线 切换的
         */
        cbOnlineStatus.setOnCheckedChangeListener { _, isChecked ->
            val target = if (isChecked) 1 else 0
            if (BaseApplication.instance().mUserModel != null && BaseApplication.instance().mUserModel!!.isOnline != target) {
                mViewModel.changeOnlineStatus()
            }
        }
    }

    override fun initObserver() {
        //退出登录
        mViewModel.mExitLoginFinish.observe(this, Observer {
            //发送登录状态改变事件
            LiveDataBus.with(LiveDataBusKey.EXIT_LOGIN_KEY).postValue(true)
            finish()
        })
        //获取用户信息
        mViewModel.mUserModelResult.observe(this, Observer {
            initUserInfo()
        })

        mViewModel.mModifyUserInfoResult.observe(this, Observer {
            putSpValue(USER_MODEL_KEY, it)
        })

        LiveDataBus.with(LiveDataBusKey.LOGIN_KEY, Boolean::class.java).observe(this, Observer {
            initUserInfo()
        })
        LiveDataBus.with(LiveDataBusKey.USER_INFO_MODIFY, Boolean::class.java)
            .observe(this, Observer {
                initUserInfo()
            })
    }

    /**
     * 初始化用户显示信息
     */
    private fun initUserInfo() {
        if (BaseApplication.instance().mUserModel == null) {
            tvUserName.text = "点击登录/注册"
            ivHeaderView.setBackgroundResource(R.mipmap.icon_user_center_header)
        } else {
            ivHeaderView.loadCircleImage(
                BASE_URL.substring(
                    0,
                    BASE_URL.length - 1
                ) + BaseApplication.instance().mUserModel!!.headUrl,
                holder = R.mipmap.icon_user_center_header
            )
            cbOnlineStatus.isChecked = BaseApplication.instance().mUserModel!!.isOnline == 1
            tvUserName.text = BaseApplication.instance().mUserModel!!.nickName
        }
    }
}