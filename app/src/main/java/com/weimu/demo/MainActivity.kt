package com.weimu.demo

import android.os.Bundle
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
                url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1555410316037&di=addbbd286d6579835b233c9371ae9002&imgtype=0&src=http%3A%2F%2Fimg.wx.lengxiaohua.cn%2Fweixin%2F201505%2Ffdde55fca65b0915de13418a5de71f88.jpg"
            )
        }

    }
}
