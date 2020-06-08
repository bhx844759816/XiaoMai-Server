package com.guangzhida.xiaomai.server.ui

import android.graphics.Color
import android.text.Selection
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.lifecycle.Observer
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.putSpValue
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.ktxlibrary.span.KtxSpan
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.USER_MODEL_KEY
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey
import com.guangzhida.xiaomai.server.ui.viewmodel.LoginViewModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initView() {

    }

    override fun initListener() {
        toolBar.setNavigationOnClickListener {
            finish()
        }
        //忘记密码
        tvForgetPassword.clickN {

        }
        //去注册
        tvRegister.clickN {
            startKtxActivity<RegisterActivity>()
        }
        //登录
        tvLogin.setOnClickListener {
            val phone = etInputPhone.text.toString().trim()
            val password = etInputPassword.text.toString().trim()
            if (phone.isEmpty()) {
                ToastUtils.toastShort("请输入手机号")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                ToastUtils.toastShort("请输入密码")
                return@setOnClickListener
            }
            mViewModel.doLogin(phone, password)
        }
        KtxSpan()
            .with(tvRegister)
            .text("还没有账号?  ", isNewLine = false)
            .text("立即注册", foregroundColor = Color.parseColor("#9245ED"), isNewLine = false).show { }
    }

    override fun initObserver() {
        mViewModel.mLoginResult.observe(this, Observer {
            //保存用户信息
            putSpValue(USER_MODEL_KEY, it)
        })
        mViewModel.mChatLoginResult.observe(this, Observer {
            mViewModel.dismissDialog()
            if (it) {
                LiveDataBus.with(LiveDataBusKey.LOGIN_KEY).postValue(true)
                finish()
            }
        })
    }

}