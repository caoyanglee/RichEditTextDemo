package com.weimu.demo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.ktx.getNetWorkInfo
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : BaseActivity() {
    override fun getLayoutResID(): Int = R.layout.activity_preview

    private var content = ""

    companion object {

        fun newIntent(context: Context, content: String) = Intent(context, PreviewActivity::class.java).apply {
            putExtra("content", content)
        }
    }

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        content = intent.getStringExtra("content")
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        val settings = mWebView.settings
        settings.javaScriptEnabled = true//支持javaScript
        //settings.setAllowFileAccess(true);//允许访问文件数据
        //settings.setBuiltInZoomControls(true);//设置支持缩放
        //settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true)
        settings.blockNetworkImage = false
        settings.domStorageEnabled = true//开启DOM缓存，关闭的话H5自身的一些操作是无效的
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.loadsImagesAutomatically = true//在页面装载完成之后再去加载图片。
        settings.allowUniversalAccessFromFileURLs = true
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        //检查是否有网络
        val info = this.getNetWorkInfo()
        if (info != null && info.isAvailable) {
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        } else {
            settings.cacheMode = WebSettings.LOAD_CACHE_ONLY//不使用网络，只加载缓存
        }

        mWebView.requestFocus()
        mWebView.setBackgroundColor(0)
        mWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY//滚动条样式

        mWebView.loadData(content, "text/html", "utf-8")
    }
}
