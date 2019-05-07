package com.weimu.richedittext

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import com.orhanobut.logger.Logger
import org.jetbrains.annotations.NotNull
import java.lang.reflect.Field

/**
 * Author:你需要一台永动机
 * Date:2019/4/10 10:41
 * Description:
 */
class RichEditText : WebView, LifecycleObserver {

    private val SETUP_HTML = "file:///android_asset/editor.html"
    private var mContents: String = ""

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        //配置
        this.settings.apply {
            this.javaScriptEnabled = true//支持javaScript
            this.setAppCacheEnabled(true)
            this.domStorageEnabled = true//开启DOM缓存，关闭的话H5自身的一些操作是无效的
            this.cacheMode = WebSettings.LOAD_DEFAULT
            this.loadsImagesAutomatically = true//在页面装载完成之后再去加载图片。
            this.allowUniversalAccessFromFileURLs = true
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                this.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            }
            this.blockNetworkImage = false
            this.loadWithOverviewMode = true
            this.useWideViewPort = true
        }

        //基础设置
        this.apply {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            webViewClient = EditorWebViewClient()
            webChromeClient = EditorWebChromeClient()
            addJsBridge(JSBridge(), "JSBridge")
        }
        loadUrl(SETUP_HTML);
    }


    private inner class JSBridge {
        @JavascriptInterface
        fun showSource(html: String) {
            mContents = html
        }
    }

    /**
     * 安卓调用js方法 2种
     * 1:无返回值 loadUrl
     * 2:有返回值 evaluateJavascript
     */
    fun exec(jsStr: String, jsCallBack: ((response: String) -> Unit)? = null) {
        val rs = "javascript:$jsStr"
        if (jsCallBack == null) {
            this.loadUrl(rs)//无返回值
        } else {
            this.evaluateJavascript(rs, jsCallBack)
        }
    }


    //获取内容
    fun getHtml() = mContents

    //设置内容
    fun setHtml(content: String) {
        exec("RE.setHtml('$content');")
    }

    //设置hint
    fun setPlaceholder(placeholder: String) {
        exec("RE.setPlaceholder('$placeholder');")
    }

    //插入图片
    fun addImage(url: String, alt: String = "") {
        //exec("javascript:RE.prepareInsert();")
        exec("RE.addImage('$url', '$alt');")
        Handler().postDelayed({ scroll2Bottom() }, 300)
        Handler().postDelayed({ showKeyBoard() }, 400)
    }


    //显示键盘
    private fun showKeyBoard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.requestFocus()
        inputMethodManager.showSoftInput(this, 0) //强制显示键盘
    }

    //增加JSBride
    @SuppressLint("JavascriptInterface")
    fun addJsBridge(jsBridge: Any, name: String) {
        this.addJavascriptInterface(jsBridge, name)
    }

    //滚动到底部
    fun scroll2Bottom() {
        scrollTo(0, computeVerticalScrollRange())
    }


    protected inner class EditorWebViewClient : WebViewClient()

    protected inner class EditorWebChromeClient : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            Logger.d(consoleMessage?.message())
            return super.onConsoleMessage(consoleMessage)
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(@NotNull owner: LifecycleOwner) {
        //防止后台无法释放js导致耗电
        this.settings.javaScriptEnabled = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onDestroy(@NotNull owner: LifecycleOwner) {
        this.webViewClient = null
        this.settings.javaScriptEnabled = false
        this.stopLoading()
        releaseAllWebViewCallback()
    }

    //释放访内存
    private fun releaseAllWebViewCallback() {
        try {
            val sConfigCallback: Field? =
                Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback")
            sConfigCallback?.isAccessible = true
            sConfigCallback?.set(null, null)
        } catch (e: NoSuchFieldException) {
            //doNothing
        } catch (e: ClassNotFoundException) {
            //doNothing
        } catch (e: IllegalAccessException) {
            //doNothing
        }

    }
}