package com.guangzhida.xiaomai.server.ui.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.model.SchoolDetailModel

/**
 * 学校列表适配器
 */
class SchoolListAdapter(list: MutableList<SchoolDetailModel>) :
    BaseQuickAdapter<SchoolDetailModel, BaseViewHolder>(
        R.layout.adapter_school_list_item_layout, list
    ) {
    var mClickItemCallBack: ((SchoolDetailModel) -> Unit)? = null
    var mLongClickItemCallBack: ((SchoolDetailModel, View) -> Unit)? = null
    override fun convert(helper: BaseViewHolder, item: SchoolDetailModel) {
        helper.setText(R.id.tvSchoolName, item.schoolName)
        helper.setText(R.id.tvDeviceError, item.errorNum)
        helper.setText(R.id.tvDeviceNormal, item.successNum)
        helper.getView<TextView>(R.id.tvSchoolName).clickN {
            mClickItemCallBack?.invoke(item)
        }
        helper.getView<TextView>(R.id.tvSchoolName).setOnLongClickListener {
            mLongClickItemCallBack?.invoke(item, it)
            true
        }
    }
}