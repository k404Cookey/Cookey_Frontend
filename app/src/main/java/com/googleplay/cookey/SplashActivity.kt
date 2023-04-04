package com.googleplay.cookey

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.googleplay.cookey.login.LoginActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 일정 시간 이후에 MainActivity 실행
        Handler().postDelayed({
            val i: Intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(i)

            // Splash 화면 종료
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        // Splash 화면에 보여질 시간 설정 (ms)
        private const val SPLASH_TIME_OUT = 2000
    }

}
