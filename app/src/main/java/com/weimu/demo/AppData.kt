package com.weimu.demo

import com.weimu.universalview.OriginAppData

/**
 * Author:你需要一台永动机
 * Date:2019/4/16 11:58
 * Description:
 */
class AppData : OriginAppData() {
    override fun isDebug(): Boolean = BuildConfig.DEBUG
}