package com.guangzhida.xiaomai.server.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.model.DeviceDetailModel

/**
 * 设备列表的适配器
 */
class DeviceListAdapter(list: MutableList<DeviceDetailModel>) :
    BaseQuickAdapter<DeviceDetailModel, BaseViewHolder>(
        R.layout.adapter_device_list_item_layout,
        list
    ) {
    var mClickItemCallBack: ((DeviceDetailModel) -> Unit)? = null
    var mLongClickItemCallBack: ((DeviceDetailModel, View) -> Unit)? = null
    override fun convert(helper: BaseViewHolder, item: DeviceDetailModel) {
        helper.setText(R.id.tvDeviceName, item.name)
        val ivDeviceState= helper.getView<ImageView>(R.id.ivDeviceState)
        if(item.status == 0){
            ivDeviceState.setBackgroundResource(R.drawable.shape_list_item_red_dot)
        }else if(item.status == 1){
            ivDeviceState.setBackgroundResource(R.drawable.shape_list_item_green_dot)
        }else if(item.status == 2){
            ivDeviceState.setBackgroundResource(R.drawable.shape_list_item_blue_dot)
        }
        helper.getView<TextView>(R.id.tvDeviceName).clickN {
            mClickItemCallBack?.invoke(item)
        }
        helper.getView<TextView>(R.id.tvDeviceName).setOnLongClickListener {
            mLongClickItemCallBack?.invoke(item,it)
            true
        }
    }
}