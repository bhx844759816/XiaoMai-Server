package com.guangzhida.xiaomai.server.ui.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginBottom
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.flexbox.FlexboxLayout
import com.guangzhida.xiaomai.ktxlibrary.ext.dp2px
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.model.ServiceHelpItemModel

/**
 * 服务帮助适配器
 */
class ServerHelpAdapter(list: MutableList<Pair<String, List<ServiceHelpItemModel>>>) :
    BaseQuickAdapter<Pair<String, List<ServiceHelpItemModel>>, BaseViewHolder>(
        R.layout.adapter_server_help_item_layout,
        list
    ) {
    var mItemClickCallBack: ((ServiceHelpItemModel) -> Unit)? = null
    override fun convert(helper: BaseViewHolder, item: Pair<String, List<ServiceHelpItemModel>>) {
        val flexParent = helper.getView<FlexboxLayout>(R.id.flexBoxLayout)
        helper.setText(R.id.tvTitle, item.first)
        flexParent.removeAllViews()
        item.second.forEach {
            addFlexItemLayout(it, flexParent)
        }
    }

    /**
     *
     */
    private fun addFlexItemLayout(item: ServiceHelpItemModel, flexboxLayout: FlexboxLayout) {
        val textView = TextView(context).apply {
            text = item.name;
            gravity = Gravity.CENTER;
            setTextColor(Color.parseColor("#333333"));
            setBackgroundResource(R.drawable.shape_hollow_fillet_rectangle_default)
            textSize = 12f
            setOnClickListener {
                mItemClickCallBack?.invoke(item)
            }
            layoutParams = FlexboxLayout.LayoutParams(context.dp2px(60), context.dp2px(30))

        }
        flexboxLayout.addView(textView)
//        val params: ViewGroup.LayoutParams = textView.layoutParams
//        if (params is FlexboxLayout.LayoutParams) {
//            params.flexBasisPercent = 0.3f
//        }
    }
}