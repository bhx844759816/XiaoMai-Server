package com.guangzhida.xiaomai.server.ui

import android.app.Activity
import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {
    private var mAgentWeb: AgentWeb? = null
    //标题的WebChromeClient
    private var mTitleWebClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            tvWebTitle.text = title ?: ""
        }
    }
    //WebViewClient
    private var mWebChromeClient = object : WebViewClient() {
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed();
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val url = intent.getStringExtra("url")
        initListener()
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(llWebParent, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setWebChromeClient(mTitleWebClient)
            .setWebViewClient(mWebChromeClient)
            .createAgentWeb()
            .ready()
            .go(url)
        mAgentWeb?.agentWebSettings?.webSettings?.setSupportZoom(true)//设置可以支持缩放
        mAgentWeb?.agentWebSettings?.webSettings?.builtInZoomControls = true//设置出现缩放工具
        mAgentWeb?.agentWebSettings?.webSettings?.useWideViewPort = true//扩大比例的缩放
        mAgentWeb?.agentWebSettings?.webSettings?.displayZoomControls = false//隐藏缩放控件
    }

    private fun initListener() {
        ivCancel.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
        toolBar.setNavigationOnClickListener {
            if (mAgentWeb?.back() != true) {
                setResult(Activity.RESULT_OK)
                finish()
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