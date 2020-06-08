package com.guangzhida.xiaomai.server.ui

import android.os.CountDownTimer
import androidx.lifecycle.Observer
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.isPhone
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.model.SchoolModel
import com.guangzhida.xiaomai.server.ui.viewmodel.RegisterViewModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_register_layout.*

/**
 * 注册界面
 */
class RegisterActivity : BaseActivity<RegisterViewModel>() {
    private val mSchoolList = mutableListOf<SchoolModel>()
    private var mSelectSchoolModel: SchoolModel? = null
    private var mTimer: MyCountDownTimer? = null
    override fun getLayoutId(): Int = R.layout.activity_register_layout

    override fun initView() {
        mViewModel.getSchoolList()
    }

    override fun initListener() {
        toolBar.setNavigationOnClickListener {
            finish()
        }
        //点击选择学校
        tvSelectSchool.clickN {
            showSchoolListPopup()
        }
        //注册
        tvRegister.clickN {
            val phone = etInputPhone.text.toString().trim()
            val password = etInputPassword.text.toString().trim()
            val code = etInputSmsCode.text.toString().trim()
            if (mSelectSchoolModel == null) {
                ToastUtils.toastShort("请选择学校")
                return@clickN
            }

            if (!phone.isPhone()) {
                ToastUtils.toastShort("请输入正确的手机号")
                return@clickN
            }
            if (password.isEmpty()) {
                ToastUtils.toastShort("请输入密码")
                return@clickN
            }
            if (code.isEmpty()) {
                ToastUtils.toastShort("请输入验证码")
                return@clickN
            }
            mViewModel.register(mSelectSchoolModel!!.id, phone, password, code)
        }
        //发送验证码
        tvSendSmsCode.clickN {
            val phone = etInputPhone.text.toString().trim()
            if (!phone.isPhone()) {
                ToastUtils.toastShort("请输入正确的手机号")
                return@clickN
            }
            mViewModel.sendSmsCode(phone)
        }
    }

    override fun initObserver() {
        //获取学校列表
        mViewModel.mSchoolListObserver.observe(this, Observer {
            mSchoolList.clear()
            mSchoolList.addAll(it)
        })
        //发送验证码结果
        mViewModel.mSmsSendResultObserver.observe(this, Observer {
            if (it) {
                mTimer = MyCountDownTimer(60000, 1000)
                mTimer?.start()
            }
        })
        //注册
        mViewModel.mRegisterResultObserver.observe(this, Observer {
            if (it) {
                finish()
            }
        })
    }

    /**
     * 展示学校的列表
     */
    private fun showSchoolListPopup() {
        if (mSchoolList.isEmpty()) {
            mViewModel.getSchoolList()
            return
        }
        val list = mSchoolList.map {
            it.name
        }.toTypedArray()
        XPopup.Builder(this)
            .atView(tvSelectSchool)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
            .hasShadowBg(false)
            .isCenterHorizontal(true)
            .popupAnimation(null)
            .asAttachList(list, null) { position, _ ->
                mSelectSchoolModel = mSchoolList[position]
                tvSelectSchool.text = mSelectSchoolModel?.name
            }
            .show()
    }


    //倒计时函数
    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(l: Long) { //防止计时过程中重复点击
            tvSendSmsCode.isClickable = false
            tvSendSmsCode.setBackgroundResource(R.drawable.shape_btn_no_clickable_bg)
            tvSendSmsCode.text = buildString {
                append(l / 1000)
                append("秒")
            }
        }

        override fun onFinish() { //重新给Button设置文字
            tvSendSmsCode.text = "重新获取"
            //设置可点击
            tvSendSmsCode.isClickable = true
            //
            tvSendSmsCode.setBackgroundResource(R.drawable.shape_btn_default_bg)
        }
    }
}