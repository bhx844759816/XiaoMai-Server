package com.guangzhida.xiaomai.server.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.utils.ToastUtils
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {
    lateinit var viewModel: VM
    //是否第一次加载
    private var isFirst: Boolean = true

    abstract fun layoutId(): Int

    private var dialog: MaterialDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(layoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVisible()
        createViewModel()
        lifecycle.addObserver(viewModel)
        initView(savedInstanceState)
        initListener()
        registerUIChange()
        initObserver()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
            isFirst = false
        }
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    open fun initView(savedInstanceState: Bundle?) {}

    open fun initListener() {

    }

    /**
     * 初始化观察者
     */
    open fun initObserver() {

    }

    /**
     * 懒加载
     */
    open fun lazyLoadData() {}


    /**
     * 注册 UI 事件
     */
    private fun registerUIChange() {
        viewModel.defUI.showDialog.observe(viewLifecycleOwner, Observer {
            showLoading()
        })
        viewModel.defUI.dismissDialog.observe(viewLifecycleOwner, Observer {
            dismissLoading()
        })
        viewModel.defUI.toastEvent.observe(viewLifecycleOwner, Observer {
            ToastUtils.toastShort(it)
        })
        viewModel.defUI.msgEvent.observe(viewLifecycleOwner, Observer {
            handleEvent(it)
        })
    }

    open fun handleEvent(msg: Message) {

    }

    /**
     * 打开等待框
     */
    private fun showLoading() {
        if (dialog == null) {
            context?.let {
                dialog = MaterialDialog(it)
                    .cancelable(false)
                    .cornerRadius(8f)
                    .customView(
                        R.layout.dialog_custom_progress_dialog_view,
                        noVerticalPadding = true
                    )
                    .lifecycleOwner(this)
                    .maxWidth(R.dimen.dialog_loading_width)
            }
        }
        dialog?.show()

    }

    /**
     * 关闭等待框
     */
    private fun dismissLoading() {
        dialog?.run { if (isShowing) dismiss() }
    }

    /**
     * 创建 ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    private fun createViewModel() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[0]
            val tClass = tp as? Class<VM> ?: BaseViewModel::class.java
            viewModel = ViewModelProvider(this).get(tClass) as VM
        }
    }
}