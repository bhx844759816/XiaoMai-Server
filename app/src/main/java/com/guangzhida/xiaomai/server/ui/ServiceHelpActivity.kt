package com.guangzhida.xiaomai.server.ui

import android.webkit.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.getSpValue
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.putSpValue
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.SCHOOL_NETWORK_ACCOUNT
import com.guangzhida.xiaomai.server.SCHOOL_NETWORK_PASSWORD
import com.guangzhida.xiaomai.server.SCHOOL_NETWORK_SERVER
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.model.ServiceHelpItemModel
import com.guangzhida.xiaomai.server.ui.adapter.ServerHelpAdapter
import com.guangzhida.xiaomai.server.ui.viewmodel.ServiceHelpViewModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.guangzhida.xiaomai.server.view.NestedScrollAgentWebView
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebViewClient
import kotlinx.android.synthetic.main.activity_service_help_layout.*
import kotlinx.android.synthetic.main.layout_service_help_main_content.*
import java.util.*

/**
 * 校园网
 */
class ServiceHelpActivity : BaseActivity<ServiceHelpViewModel>() {
    private val mItemList = mutableListOf<Pair<String, List<ServiceHelpItemModel>>>()
    private lateinit var mServer: String
    private lateinit var mAccount: String
    private lateinit var mPassword: String
    private val mUrlStack = Stack<String>()
    private var mAgentWeb: AgentWeb? = null
    private var isLoading = false //只有第一次loading 后面不进行重定向到指定的地址
    private var mNeedRedirectTitle = ""; //需要重定向到地址的标题
    private var mNeedRedirectName = "";//需要重定向到地址的名字
    private var mNeedRedirectUrl = "" //需要重定向的地址
    private val mAdapter by lazy {
        ServerHelpAdapter(mItemList)
    }
    private var mWebChromeClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            LogUtils.i("shouldOverrideUrlLoading url=$url")
            if (url != null && url == "$mServer/lfradius/login.php/login/showright" && mNeedRedirectUrl.isNotEmpty() && !isLoading) {
                mAgentWeb?.urlLoader?.loadUrl(mNeedRedirectUrl)
            } else if (url == mNeedRedirectUrl) {
                isLoading = true
            }
            super.onPageFinished(view, url)
        }
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            LogUtils.i("shouldOverrideUrlLoading url=$url")
            if (url != null && url == "$mServer/lfradius/login.php/login/showright" && mNeedRedirectUrl.isNotEmpty() && !isLoading) {
                mAgentWeb?.urlLoader?.loadUrl(mNeedRedirectUrl)
            } else if (url == mNeedRedirectUrl) {
                isLoading = true
            }
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }

    }

    override fun getLayoutId(): Int = R.layout.activity_service_help_layout

    override fun initView() {
        mNeedRedirectTitle = intent.getStringExtra("NeedRedirectTitle")
        mNeedRedirectName = intent.getStringExtra("NeedRedirectName")
        mServer = getSpValue(SCHOOL_NETWORK_SERVER, "")
        mAccount = getSpValue(SCHOOL_NETWORK_ACCOUNT, "")
        mPassword = getSpValue(SCHOOL_NETWORK_PASSWORD, "")
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(llWebParent, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DERECT)
            .setWebView(NestedScrollAgentWebView(this))
            .setWebViewClient(mWebChromeClient)
            .createAgentWeb()
            .ready()
            .get()
        recyclerView.adapter = mAdapter
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        mAgentWeb?.agentWebSettings?.webSettings?.userAgentString = buildString {
            append(mAgentWeb?.agentWebSettings?.webSettings?.userAgentString)
            append(";LFRadius(webclient)")
        }
        mAgentWeb?.webCreator?.webView?.clearHistory()
        mAgentWeb?.webCreator?.webView?.clearFormData()
        mAgentWeb?.agentWebSettings?.webSettings?.domStorageEnabled = true
        mAgentWeb?.agentWebSettings?.webSettings?.javaScriptEnabled = true
        mAgentWeb?.agentWebSettings?.webSettings?.useWideViewPort = true
        mAgentWeb?.agentWebSettings?.webSettings?.loadWithOverviewMode = true
        mAgentWeb?.agentWebSettings?.webSettings?.savePassword = false
        mAgentWeb?.agentWebSettings?.webSettings?.saveFormData = false
        mAgentWeb?.agentWebSettings?.webSettings?.setSupportZoom(true)//设置可以支持缩放
        mAgentWeb?.agentWebSettings?.webSettings?.builtInZoomControls = true//设置出现缩放工具
        mAgentWeb?.agentWebSettings?.webSettings?.useWideViewPort = true//扩大比例的缩放
        mAgentWeb?.agentWebSettings?.webSettings?.displayZoomControls = false//隐藏缩放控件
        //当未发现本地配置的账号信息就弹出对话框让用户输入
        if (mServer.isEmpty() || mAccount.isEmpty() || mPassword.isEmpty()) {
            showLoadingDialog()
        } else {
            initLoginHtml()
        }
    }

    /**
     * 注入登录的Html进行登录
     */
    private fun initLoginHtml() {
        val html = buildString {
            append("<html><head><meta charset='gbk'></head><body><meta name='renderer' content='webkit'><meta name='viewport' content='width=device-width, initial-scale=1'>")
            append("正在自动登录[")
            append(mServer)
            append("]请稍候...<form  method='post' id='__admin_login' name='__admin_login' action='")
            append(mServer)
            append("/lfradius/libs/app/app.php'>")
            append("<input type='hidden' name='username' value='")
            append(mAccount)
            append("'>")
            append("<input type='hidden' name='password'  value='")
            append(mPassword)
            append("'>")
            append("<input type='hidden' name='run' value='admin_login'>")
            append("</form>")
            append("<script type='text/javascript'>document.forms['__admin_login'].submit();</script>")
            append("</body></html")
        }
        mAgentWeb?.urlLoader?.loadDataWithBaseURL(
            "about:blank",
            html,
            "text/html; charset=UTF-8",
            "UTF-8",
            null
        )
        val params =
            mapOf("username" to mAccount, "password" to mPassword, "run" to "admin_get_level")
        mViewModel.getServiceHelpItemList("$mServer/lfradius/libs/app/app.php", params)
    }

    override fun initListener() {
        mAdapter.mItemClickCallBack = {
            drawerLayout.closeDrawer(GravityCompat.END)
            val url = "$mServer/lfradius/login.php/${it.url}"
            mAgentWeb?.urlLoader?.loadUrl(url)
        }
    }

    override fun initObserver() {
        mViewModel.mContentListObserver.observe(this, Observer {
            mItemList.clear()
            mItemList.addAll(it)
            it.forEach { item ->
                if (item.first == mNeedRedirectTitle) {
                    val url = item.second.find { serviceHelpItemModel ->
                        serviceHelpItemModel.name == mNeedRedirectName
                    }?.url
                    mNeedRedirectUrl = "$mServer/lfradius/login.php/$url"
                }
            }
            mAdapter.notifyDataSetChanged()
        })
        //弹出
        ivMenu.clickN {
            drawerLayout.openDrawer(GravityCompat.END)
        }
        //刷新
        ivRefresh.clickN {
            mAgentWeb?.urlLoader?.reload()
        }
        toolBar.setNavigationOnClickListener {
            finish()
        }
        tvExitLogin.clickN {
            CookieSyncManager.createInstance(this);
            CookieManager.getInstance().removeAllCookie();
            putSpValue(SCHOOL_NETWORK_SERVER, "")
            putSpValue(SCHOOL_NETWORK_ACCOUNT, "")
            putSpValue(SCHOOL_NETWORK_PASSWORD, "")
            finish()
        }
    }


    private fun showLoadingDialog() {
        MaterialDialog(this)
            .cornerRadius(res = R.dimen.dialog_corner_radius)
            .customView(
                viewRes = R.layout.dialog_service_help_login_layout,
                noVerticalPadding = true
            )
            .lifecycleOwner(this)
            .show {
                val inputServer = getCustomView().findViewById<EditText>(R.id.etInputServer)
                val inputAccount = getCustomView().findViewById<EditText>(R.id.etInputAccount)
                val inputPassword = getCustomView().findViewById<EditText>(R.id.etInputPassword)
                val tvConfirm = getCustomView().findViewById<TextView>(R.id.tvConfirm)
                tvConfirm.clickN {
                    val server = inputServer.text.toString().trim()
                    val account = inputAccount.text.toString().trim()
                    val password = inputPassword.text.toString().trim()
                    if (server.isEmpty() || !server.startsWith("http")) {
                        ToastUtils.toastShort("服务器地址不能为空且必须以http起始")
                        return@clickN
                    }
                    if (account.isEmpty()) {
                        ToastUtils.toastShort("账号不能为空")
                        return@clickN
                    }
                    if (password.isEmpty()) {
                        ToastUtils.toastShort("密码不能为空")
                        return@clickN
                    }
                    mServer = server
                    mAccount = account
                    mPassword = password
                    putSpValue(SCHOOL_NETWORK_SERVER, server)
                    putSpValue(SCHOOL_NETWORK_ACCOUNT, account)
                    putSpValue(SCHOOL_NETWORK_PASSWORD, password)
                    initLoginHtml()
                    dismiss()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        mAgentWeb?.webLifeCycle?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mAgentWeb?.webLifeCycle?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAgentWeb?.webLifeCycle?.onDestroy()
    }

}