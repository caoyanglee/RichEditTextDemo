package com.weimu.demo

import android.os.Bundle
import android.os.Handler
import com.orhanobut.logger.Logger
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.ktx.setOnClickListenerPro
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun getLayoutResID(): Int = R.layout.activity_main

    override fun afterViewAttach(savedInstanceState: Bundle?) {

        lifecycle.addObserver(mRichEditText)
        mBtnOutHtml.setOnClickListenerPro {
            Logger.d("${mRichEditText.getHtml()}")
        }


        mBtnAddImage.setOnClickListenerPro {
            mRichEditText.addImage(
                url = "http://sjbz.fd.zol-img.com.cn/t_s320x510c/g5/M00/04/04/ChMkJ1jctw-IGJY8AAMI_lmg3L0AAbNOwPrPvsAAwkW968.jpg"
            )
        }


        mBtn3.setOnClickListenerPro {
            mRichEditText.setHtml("<p>吃屎吧你</p>")
        }

        mBtn4.setOnClickListenerPro {
            startActivity(PreviewActivity.newIntent(this@MainActivity, mRichEditText.getHtml()))
        }


        Handler().postDelayed({
            mRichEditText.focus()
        }, 300)
    }
}
