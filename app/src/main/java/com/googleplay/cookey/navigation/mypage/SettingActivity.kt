package com.googleplay.cookey.navigation.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.googleplay.cookey.App
import com.googleplay.cookey.R
import com.googleplay.cookey.base.BaseActivity
import com.googleplay.cookey.databinding.ActivitySettingBinding
import com.googleplay.cookey.databinding.ActivityUploadBinding
import com.googleplay.cookey.login.LoginActivity

//import com.googleplay.yorijori.login.LoginActivity
//import com.kakao.sdk.user.UserApiClient


class SettingActivity : BaseActivity(R.layout.activity_setting) {

    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibSettingBack.setOnClickListener {
            finish()
        }



        binding.tvLogout.setOnClickListener {
            val intent = Intent(App.instance, LoginActivity::class.java)
            startActivity(intent)
//            UserApiClient.instance.logout { error ->
//                if (error != null) {
//                    Log.e("logout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
//                }
//                else {
//                    Log.i("logout", "로그아웃 성공. SDK에서 토큰 삭제됨")
//                    val intent = Intent(App.instance, LoginActivity::class.java )
//                    startActivity(intent)
//                }
//            }
        }

        binding.tvExit.setOnClickListener {
            val intent = Intent(App.instance, LoginActivity::class.java)
            startActivity(intent)
//            UserApiClient.instance.unlink { error ->
//                if (error != null) {
//                    Log.e("exit", "연결 끊기 실패", error)
//                }
//                else {
//                    Log.i("exit", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
//                    val intent = Intent(App.instance, LoginActivity::class.java )
//                    startActivity(intent)
//                }
//            }
        }



    }

}