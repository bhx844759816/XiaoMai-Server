package com.guangzhida.xiaomai.server.ui.adapter

import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.gone
import com.guangzhida.xiaomai.ktxlibrary.ext.toTimeStamp
import com.guangzhida.xiaomai.ktxlibrary.ext.visible
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.model.DeviceDetailModel
import com.guangzhida.xiaomai.server.model.SchoolCardModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import java.util.*

/**
 * 办理校园卡的适配器
 */
class SchoolCardListAdapter(list: MutableList<SchoolCardModel>) :
    BaseQuickAdapter<SchoolCardModel, BaseViewHolder>(
        R.layout.adapter_school_card_list_item_layout,
        list
    ), LoadMoreModule {
    var mCallPhoneCallBack: ((String) -> Unit)? = null
    var mOperateCallBack: ((String, SchoolCardModel) -> Unit)? = null
    override fun convert(helper: BaseViewHolder, item: SchoolCardModel) {
        val llOperateParent = helper.getView<LinearLayout>(R.id.llOperateParent)
        val tvOperateState = helper.getView<TextView>(R.id.tvOperateState)
        val tvCallPhone = helper.getView<TextView>(R.id.tvCallPhone)
        helper.setText(R.id.tvName, item.userName ?: "未知姓名")
        helper.setText(R.id.tvPeopleCard, item.idCardNo ?: "未知身份证号")
        helper.setText(R.id.tvSchoolName, item.schoolName ?: "未知学校")
        helper.setText(R.id.tvDate, item.createTime.toTimeStamp("yyyy年MM月dd日"))
        if (item.status == "0") {
            llOperateParent.visible()
            tvOperateState.gone()
        } else {
            if (item.status == "1") {
                tvOperateState.text = "已处理"
            } else if (item.status == "2") {
                tvOperateState.text = "处理失败"
            }
            tvOperateState.visible()
            llOperateParent.gone()
        }
        tvCallPhone.clickN {
            mCallPhoneCallBack?.invoke(item.mobilePhone ?: "")
        }
        helper.getView<TextView>(R.id.tvOperateSuccess).clickN {
            mOperateCallBack?.invoke("1", item)
        }
        helper.getView<TextView>(R.id.tvOperateFail).clickN {
            mOperateCallBack?.invoke("2", item)
        }
    }
}