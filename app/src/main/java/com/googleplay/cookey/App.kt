package com.googleplay.cookey

import android.app.Application
import com.googleplay.cookey.SharedPreferenceUtil
import com.jaeger.library.StatusBarUtil
//import com.kakao.sdk.common.KakaoSdk

/**
 * instance를 반환하는 class
 * Context가 필요할 때 사용하면 됩니다!!
 * App.intance
 */

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        sharedPrefs = SharedPreferenceUtil(applicationContext)

        //KakaoSdk.init(instance, "7c0ae88d49666eca35738b64bba94021")
    }

    companion object {
        lateinit var instance : App
            private set
        lateinit var sharedPrefs: SharedPreferenceUtil
        lateinit var statusBar : StatusBarUtil
    }
}