package com.guangzhida.xiaomai.server.ui

import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.gone
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.ktxlibrary.ext.visible
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey.DEVICE_STATE_UPDATE
import com.guangzhida.xiaomai.server.model.DeviceDetailModel
import com.guangzhida.xiaomai.server.ui.adapter.DeviceListAdapter
import com.guangzhida.xiaomai.server.ui.viewmodel.DeviceListViewModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_device_list_layout.*

/**
 * 设备列表
 */
class DeviceListActivity : BaseActivity<DeviceListViewModel>() {
    private lateinit var mSchoolId: String
    private val mDeviceList = mutableListOf<DeviceDetailModel>()
    private val mAdapter by lazy {
        DeviceListAdapter(mDeviceList)
    }


    override fun getLayoutId(): Int = R.layout.activity_device_list_layout

    override fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        mSchoolId = intent.getStringExtra("SchoolId")!!
        mViewModel.getDeviceList(mSchoolId)
    }

    override fun initListener() {
        toolBar.setNavigationOnClickListener {
            finish()
        }
        swipeRefresh.setOnRefreshListener {
            mViewModel.getDeviceList(mSchoolId)
        }
        mAdapter.mClickItemCallBack = {
            startKtxActivity<WebActivity>(value = Pair("url", it.internetAddress))
        }
        mAdapter.mLongClickItemCallBack = { item, view ->
            showPopupMenu(item, view)
        }
        addDevice.clickN {
            showAddDeviceBottomDialog()
        }
    }

    override fun initObserver() {
        //获取设备列表
        mViewModel.deviceDetailListObserver.observe(this, Observer {
            swipeRefresh.isRefreshing = false
            mDeviceList.clear()
            mDeviceList.addAll(it)
            mAdapter.notifyDataSetChanged()
        })
        //操作设备回调 (删除和添加)
        mViewModel.operateDeviceObserver.observe(this, Observer {
            LiveDataBus.with(DEVICE_STATE_UPDATE, Boolean::class.java).postValue(true)
            mViewModel.getDeviceList(mSchoolId)
        })
    }


    /**
     * 展示长按的PopupMenu
     */
    private fun showPopupMenu(item: DeviceDetailModel, view: View) {
        XPopup.Builder(this)
            .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
            .hasShadowBg(false)
            .isCenterHorizontal(true)
            .offsetY(-50)
            .asAttachList(arrayOf("编辑", "删除"), null) { position, _ ->
                when (position) {
                    0 -> { //编辑
                        showAddDeviceBottomDialog(item)
                    }
                    1 -> {//删除
                        showDeleteDeviceDialog(item)
                    }
                }
            }
            .show()
    }

    /**
     * 展示底部的
     */
    private fun showAddDeviceBottomDialog(item: DeviceDetailModel? = null) {
        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT))
            .customView(viewRes = R.layout.dialog_bottom_add_device_layout)
            .lifecycleOwner(this)
            .show {
                val rgSelect = this.getCustomView().findViewById<RadioGroup>(R.id.rgSelect)
                val rbSelectNet = this.getCustomView().findViewById<RadioButton>(R.id.rbSelectNet)
                val rbSelectTelent =
                    this.getCustomView().findViewById<RadioButton>(R.id.rbSelectTelent)
                val tvSave = this.getCustomView().findViewById<TextView>(R.id.tvSave)
                val etInputDeviceName =
                    this.getCustomView().findViewById<EditText>(R.id.etInputDeviceName)
                val etInputNet = this.getCustomView().findViewById<EditText>(R.id.etInputNet)
                val etInputAddress =
                    this.getCustomView().findViewById<EditText>(R.id.etInputAddress)
                val etInputPort = this.getCustomView().findViewById<EditText>(R.id.etInputPort)
                val etInputIp = this.getCustomView().findViewById<EditText>(R.id.etInputIp)
                val llTelentParent =
                    this.getCustomView().findViewById<LinearLayout>(R.id.llTelentParent)
                val llNetWorkParent =
                    this.getCustomView().findViewById<LinearLayout>(R.id.llNetWorkParent)
                item?.let {
                    etInputDeviceName.setText(it.name)
                    etInputNet.setText(it.internetAddress)
                    if (it.isTelnet) {
                        rbSelectTelent.isChecked = true
                        etInputAddress.setText(it.ip)
                        etInputPort.setText(it.port)
                        llTelentParent.visible()
                        llNetWorkParent.gone()
                    } else {
                        etInputIp.setText(it.ip)
                        llTelentParent.gone()
                        llNetWorkParent.visible()
                    }
                }
                rgSelect.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.rbSelectNet -> { //选择网址
                            llTelentParent.gone()
                            llNetWorkParent.visible()
                        }
                        R.id.rbSelectTelent -> {//选择Telent
                            llTelentParent.visible()
                            llNetWorkParent.gone()
                            //将BottomDialog全部弹出来
                            this.setPeekHeight(res = R.dimen.bottom_dialog_peek_height)
                        }
                    }
                }
                //保存并提交
                tvSave.clickN {
                    val deviceName = etInputDeviceName.text.toString().trim()
                    val address = etInputNet.text.toString().trim()
                    val port = etInputPort.text.toString().trim()
                    val isTelnet = !rbSelectNet.isChecked
                    val ip = if (isTelnet) {
                        etInputAddress.text.toString().trim()
                    } else {
                        etInputIp.text.toString().trim()
                    }
                    saveDevice(item?.id, deviceName, address, ip, port, isTelnet)
                    dismiss()
                }
            }
    }

    /**
     * 保存设备状态
     */
    private fun saveDevice(
        deviceId: String?,
        deviceName: String,
        address: String,
        ip: String,
        port: String,
        isTelnet: Boolean
    ) {
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtils.toastShort("设备名称不能为空")
            return
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtils.toastShort("网址不能为空")
            return
        }
        mViewModel.addDevice(deviceId, mSchoolId, deviceName, address, isTelnet, ip, port)
    }


    /**
     * 提示删除学校的Dialog
     */
    private fun showDeleteDeviceDialog(item: DeviceDetailModel) {
        MaterialDialog(this)
            .cornerRadius(literalDp = 8f)
            .customView(
                viewRes = R.layout.dialog_confirm_tips_layout,
                noVerticalPadding = true,
                horizontalPadding = false,
                dialogWrapContent = true
            )
            .lifecycleOwner(this)
            .show {
                val title = getCustomView().findViewById<TextView>(R.id.title)
                val tvCancel = getCustomView().findViewById<TextView>(R.id.tvCancel)
                val tvConfirm = getCustomView().findViewById<TextView>(R.id.tvConfirm)
                title.text = "确认删除设备"
                tvConfirm.clickN {
                    mViewModel.deleteDevice(item.id)
                    //删除学校
                    this.dismiss()
                }
                tvCancel.clickN {
                    this.dismiss()
                }
            }
    }
}