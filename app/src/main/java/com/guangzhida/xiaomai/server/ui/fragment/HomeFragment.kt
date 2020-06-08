package com.guangzhida.xiaomai.server.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.guangzhida.xiaomai.ktxlibrary.ext.*
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.TaskService
import com.guangzhida.xiaomai.server.base.BaseFragment
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey
import com.guangzhida.xiaomai.server.ext.loadCircleImage
import com.guangzhida.xiaomai.server.http.BASE_URL
import com.guangzhida.xiaomai.server.model.SchoolDetailModel
import com.guangzhida.xiaomai.server.ui.*
import com.guangzhida.xiaomai.server.ui.viewmodel.HomeViewModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_home_center_grid.*

class HomeFragment : BaseFragment<HomeViewModel>() {
    override fun layoutId(): Int = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initUserInfo()
    }

    override fun initListener() {
        rlSelectSchool.clickN {
            if (BaseApplication.instance().mUserModel == null) {
                startKtxActivity<LoginActivity>()
            } else {
                startKtxActivity<SchoolListActivity>()
            }
        }
        swipeRefresh.setOnRefreshListener {
            if (BaseApplication.instance().mUserModel != null) {
                getAllSchoolInfo()
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        //办理校园卡
        llSchoolCardItem.clickN {
            if (BaseApplication.instance().mUserModel != null) {
                startKtxActivity<SchoolCardListActivity>()
            } else {
                ToastUtils.toastShort("登录后才能使用该功能")
            }
        }
        //个人中心
        ivMessageNotify.clickN {
            startKtxActivity<UserActivity>()
        }
        //搜索用户
        idBindAccount.clickN {
            startKtxActivity<ServiceHelpActivity>(
                values = listOf(
                    Pair("NeedRedirectTitle", "用户管理"),
                    Pair("NeedRedirectName", "搜索用户")
                )
            )
        }
        //购买套餐
        llAccountRecharge.clickN {
            startKtxActivity<ServiceHelpActivity>(
                values = listOf(
                    Pair("NeedRedirectTitle", "运营管理"),
                    Pair("NeedRedirectName", "购买套餐")
                )
            )
        }
        //办理迁移
        llAccountTransfer.clickN {
            startKtxActivity<ServiceHelpActivity>(
                values = listOf(
                    Pair(
                        "NeedRedirectTitle", "运营管理"
                    ),
                    Pair("NeedRedirectName", "办理迁移")
                )
            )
        }
        // 套餐清零
        llAccountPackageModify.clickN {
            showClearPackageDialog()
        }
    }

    override fun initObserver() {
        viewModel.schoolListObserver.observe(this, Observer {
            if (it.isNotEmpty()) {
                val errorList = it.filter { item ->
                    item.errorNum.toInt() > 0
                }
                showNoSetMealNotifyView(errorList)
                if (errorList.isEmpty()) {
                    tvSchoolName.text = it[0].schoolName
                    ivErrorDot.gone()
                } else {
                    tvSchoolName.text = errorList[0].schoolName
                    ivErrorDot.visible()
                }
                var errorNum = 0;
                var normalNum = 0;
                it.forEach { item ->
                    errorNum += item.errorNum.toInt()
                    normalNum += item.successNum.toInt()

                }
                tvErrorNum.text = errorNum.toString()
                tvNormalNum.text = normalNum.toString()
            }
        })
        viewModel.refreshObserver.observe(this, Observer {
            swipeRefresh.isRefreshing = false
        })
        LiveDataBus.with(LiveDataBusKey.LOGIN_KEY, Boolean::class.java).observe(this, Observer {
            initUserInfo()
        })
        LiveDataBus.with(LiveDataBusKey.EXIT_LOGIN_KEY, Boolean::class.java)
            .observe(this, Observer {
                initUserInfo()
            })
        LiveDataBus.with(LiveDataBusKey.USER_INFO_MODIFY, Boolean::class.java)
            .observe(this, Observer {
                initUserInfo()
            })
    }

    /**
     * 展示套餐过期的通知
     */
    private fun showNoSetMealNotifyView(list: List<SchoolDetailModel>) {
        if (list.isEmpty()) {
            llNotifyParent.gone()
            viewFlipper.removeAllViews()
        } else {
            llNotifyParent.visible()
            viewFlipper.removeAllViews()
            list.forEach {
                val textView = TextView(context)
                textView.setTextColor(Color.parseColor("#FF0909"))
                textView.textSize = 10f
                textView.text = buildString {
                    append(it.schoolName)
                    append("设备异常检测 ! ! !")
                }
                viewFlipper.addView(textView)
            }
            if (list.size > 1) {
                viewFlipper.startFlipping()
            }
        }

    }

    /**
     * 获取全部学校的信息
     */
    private fun getAllSchoolInfo() {
        BaseApplication.instance().mUserModel?.let {
            viewModel.getAllSchoolInfo(it.id)
        }
    }

    private fun initUserInfo() {
        if (BaseApplication.instance().mUserModel != null) {
            ivMessageNotify.loadCircleImage(
                BASE_URL.substring(
                    0,
                    BASE_URL.length - 1
                ) + BaseApplication.instance().mUserModel!!.headUrl,
                holder = R.mipmap.icon_user_center_header
            )
        }
    }

    override fun onResume() {
        super.onResume()
        getAllSchoolInfo()
    }

    private fun showClearPackageDialog() {
        activity?.let {
            MaterialDialog(it)
                .cornerRadius(8f)
                .customView(
                    viewRes = R.layout.dialog_package_clear_layout,
                    noVerticalPadding = true,
                    dialogWrapContent = true,
                    horizontalPadding = false
                )
                .lifecycleOwner(it)
                .show {
                    val etInputPhone = getCustomView().findViewById<EditText>(R.id.etInputPhone)
                    val cbConfirm = getCustomView().findViewById<CheckBox>(R.id.cbConfirm)
                    val tvConfirm = getCustomView().findViewById<TextView>(R.id.tvConfirm)
                    tvConfirm.clickN {
                        val phone = etInputPhone.text.toString().trim()
                        if (!phone.isPhone()) {
                            ToastUtils.toastShort("请输入正确的手机号")
                            return@clickN
                        }
                        if (!cbConfirm.isChecked) {
                            ToastUtils.toastShort("请勾选同意清除套餐")
                            return@clickN
                        }
                        viewModel.clearPackage(phone)
                        dismiss()
                    }
                }
        }
    }

}