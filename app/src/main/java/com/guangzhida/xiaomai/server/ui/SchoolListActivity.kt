package com.guangzhida.xiaomai.server.ui

import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.event.stateChangeLiveData
import com.guangzhida.xiaomai.server.model.SchoolDetailModel
import com.guangzhida.xiaomai.server.ui.adapter.SchoolListAdapter
import com.guangzhida.xiaomai.server.ui.viewmodel.SchoolListViewModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_school_list_layout.*

/**
 * 学校列表
 */
class SchoolListActivity : BaseActivity<SchoolListViewModel>() {
    private val mUserModel = BaseApplication.instance().mUserModel!!
    private val mSchoolList = mutableListOf<SchoolDetailModel>()

    private val mAdapter by lazy {
        SchoolListAdapter(mSchoolList)
    }

    override fun getLayoutId(): Int = R.layout.activity_school_list_layout

    override fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        mViewModel.getSchoolList(mUserModel.id)
    }

    override fun initListener() {
        toolBar.setNavigationOnClickListener {
            finish()
        }
        swipeRefresh.setOnRefreshListener {
            mViewModel.getSchoolList(mUserModel.id)
        }
        //点击item
        mAdapter.mClickItemCallBack = {
            startKtxActivity<DeviceListActivity>(value = Pair("SchoolId", it.iid))
        }
        //长按
        mAdapter.mLongClickItemCallBack = { item, view ->
            showPopupMenu(item, view)
        }
        //添加学校
        addSchool.clickN {
            showAddSchoolDialog(null)
        }
    }

    override fun initObserver() {
        mViewModel.schoolListObserver.observe(this, Observer {
            mSchoolList.clear()
            mSchoolList.addAll(it)
            mAdapter.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        })
        //添加学校
        mViewModel.addSchoolObserver.observe(this, Observer {
            if (it) {
                ToastUtils.toastShort("添加学校成功")
                stateChangeLiveData.postValue(true)
            }
        })
        //删除学校
        mViewModel.deleteSchoolObserver.observe(this, Observer {
            if (it) {
                ToastUtils.toastShort("删除学校成功")
                stateChangeLiveData.postValue(true)
            }
        })
        //修改学校
        mViewModel.modifySchoolMessageObserver.observe(this, Observer {
            if (it) {
                ToastUtils.toastShort("修改学校成功")
                stateChangeLiveData.postValue(true)
            }
        })
        stateChangeLiveData.observe(this, Observer {
            mViewModel.getSchoolList(mUserModel.id)
        })
    }

    /**
     * 展示长按的PopupMenu
     */
    private fun showPopupMenu(item: SchoolDetailModel, view: View) {
        XPopup.Builder(this)
            .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
            .hasShadowBg(false)
            .isCenterHorizontal(true)
            .offsetY(-50)
            .asAttachList(arrayOf("编辑", "删除"), null) { position, _ ->
                when (position) {
                    0 -> { //编辑
                        showAddSchoolDialog(item)
                    }
                    1 -> {//删除
                        showDeleteSchoolDialog(item)
                    }
                }
            }
            .show()
    }


    private fun showAddSchoolDialog(item: SchoolDetailModel?) {
        MaterialDialog(this)
            .cornerRadius(literalDp = 8f)
            .customView(
                viewRes = R.layout.dialog_modify_school_layout,
                noVerticalPadding = true,
                horizontalPadding = false,
                dialogWrapContent = true
            )
            .lifecycleOwner(this)
            .show {
                val etInputSchoolName =
                    getCustomView().findViewById<EditText>(R.id.etInputSchoolName)
                if (item != null && item.schoolName.isNotEmpty()) {
                    etInputSchoolName.setText(item.schoolName)
                    etInputSchoolName.setSelection(item.schoolName.length)
                }
                getCustomView().findViewById<TextView>(R.id.tvCancel).clickN {
                    dismiss()
                }
                getCustomView().findViewById<TextView>(R.id.tvConfirm).clickN {
                    val schoolNameInput = etInputSchoolName.text.toString().trim()
                    if (schoolNameInput.isEmpty()) {
                        ToastUtils.toastShort("请输入学校名称")
                    }
                    if (item == null) {
                        mViewModel.addSchoolInfo(mUserModel.id, schoolNameInput)
                    } else {
                        mViewModel.updateSchool(item.iid, schoolNameInput)
                    }
                    dismiss()
                }
            }
    }

    /**
     * 提示删除学校的Dialog
     */
    private fun showDeleteSchoolDialog(item: SchoolDetailModel) {
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
                title.text = "确认删除学校"
                tvConfirm.clickN {
                    //删除学校
                    mViewModel.deleteSchool(item.iid)
                    this.dismiss()
                }
                tvCancel.clickN {
                    this.dismiss()
                }
            }
    }
}