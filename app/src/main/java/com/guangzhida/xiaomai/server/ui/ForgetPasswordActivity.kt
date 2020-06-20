package com.guangzhida.xiaomai.server.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Selection
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.lifecycle.Observer
import com.guangzhida.xiaomai.ktxlibrary.core.KtxManager
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.ui.viewmodel.ForgetPasswordViewModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_forget_password_layout.*

/**
 * 忘记密码
 */
class ForgetPasswordActivity : BaseActivity<ForgetPasswordViewModel>() {
    private var mTimer: MyCountDownTimer? = null
    override fun initView() {
        //点击返回
        toolBar.setNavigationOnClickListener {
            finish()
        }
        //发送验证码
        tvSendSmsCode.setOnClickListener {
            val phone = inputPhone.text.toString().trim()
            if (phone.isEmpty()) {
                ToastUtils.toastShort("输入的手机号不能为空")
                return@setOnClickListener
            }
            mViewModel.sendSmsCode(phone)
        }
        //确认修改密码
        tvConfirm.setOnClickListener {
            val phone = inputPhone.text.toString().trim()
            val smsCode = inputCode.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            if (phone.isEmpty()) {
                ToastUtils.toastShort("输入的手机号不能为空")
                return@setOnClickListener
            }
            if (smsCode.isEmpty()) {
                ToastUtils.toastShort("输入的验证码不能为空")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                ToastUtils.toastShort("输入的密码不能为空")
                return@setOnClickListener
            }
            mViewModel.confirmModifyPassword(phone, smsCode, password)
        }
        //改变密码的显示状态
        cbPassword.setOnCheckedChangeListener { _, _ ->
            checkPasswordShowState()
        }
        registerLiveDataObserver()
    }
    /**
     * 注册观察者
     */
    private fun registerLiveDataObserver() {
        //确认密码的观察者
        mViewModel.mConfirmPasswordResultLiveData.observe(this, Observer {
            if (it) {
                ToastUtils.toastShort("修改密码成功")
                //
            }
        })
        //发送密码的观察者
        mViewModel.mSmsCodeLiveData.observe(this, Observer {
            if (it) {
                mTimer = MyCountDownTimer(60000, 1000)
                mTimer?.start()
            }
        })
    }

    //倒计时函数
    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(l: Long) { //防止计时过程中重复点击
            tvSendSmsCode.isClickable = false
            tvSendSmsCode.text = buildString {
                append(l / 1000)
                append("秒")
            }
        }

        override fun onFinish() { //重新给Button设置文字
            tvSendSmsCode.text = "重新获取"
            //设置可点击
            tvSendSmsCode.isClickable = true
        }
    }

    private fun checkPasswordShowState() {
        val method = inputPassword.transformationMethod
        if (method === HideReturnsTransformationMethod.getInstance()) {
            inputPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        } else {
            inputPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        // 保证切换后光标位于文本末尾
        val spanText = inputPassword.text
        if (spanText != null) {
            Selection.setSelection(spanText, spanText.length)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancel()
        mTimer = null
    }

    override fun getLayoutId(): Int =  R.layout.activity_forget_password_layout
}