package com.googleplay.cookey.login


import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.googleplay.cookey.App
import com.googleplay.cookey.MainActivity
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.ActivityLoginMainBinding



class LoginActivity : AppCompatActivity() { // LoginActivity 클래스 선언

    private lateinit var binding: ActivityLoginMainBinding // ActivityLoginMainBinding 인스턴스 변수 선언
    private val RC_SIGN_IN = 0 // RC_SIGN_IN 변수에 0 할당

    lateinit var mGoogleSignInClient: GoogleSignInClient // GoogleSignInClient 인스턴스 변수 선언

    private var userToken = "" // userToken 문자열 변수 선언 및 초기화
    private var userEmail = "" // userEmail 문자열 변수 선언 및 초기화
    private val repository = Repository() // Repository 인스턴스 변수 선언
    private var userInfo = RecipeDTO.RequestPostLogin("", "") // RequestPostLogin 인스턴스 변수 선언 및 초기화

    // onCreate 함수 시작
    override fun onCreate(savedInstanceState: Bundle?) {
        // 부모 클래스의 onCreate 함수 호출
        super.onCreate(savedInstanceState)
        // ActivityLoginMainBinding 인스턴스 생성 및 초기화
        binding = ActivityLoginMainBinding.inflate(layoutInflater)
        // Activity 화면 레이아웃 설정
        setContentView(binding.root)

        // 구글 로그인 버튼 설정 함수 호출
        setGoogleLoginBtn()

        //동영상 배경 구현
        val mVideoview = findViewById<VideoView>(R.id.video_view)
        val vidioUri = Uri.parse("android.resource://${packageName}/${R.raw.bg}")
        mVideoview.setVideoURI(vidioUri)
        mVideoview.start()
//        val mVideoview = findViewById<View>(com.googleplay.cookey.R.id.video_view) as VideoView
//        mVideoview.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + com.googleplay.cookey.R.raw.bg))
//        mVideoview.start()
        mVideoview.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer) {
                mp.isLooping = true
            }
        })

    } // onCreate 함수 끝

    // 334158895250-5pf05887ntqbd55fujpk06kc6sru215o.apps.googleusercontent.com
    // Google 로그인 옵션 설정
    private fun setGoogleLoginBtn() {
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId() // 사용자 ID 요청
                .requestEmail() // 이메일 주소 요청
                .requestProfile() // 사용자 프로필 요청
                .requestIdToken(getString(R.string.client_id))
                .build()
        // GoogleSignInClient 설정
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google 로그인 버튼 클릭 리스너 등록
        binding.ivGoogleLogin.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            // startActivityForResult() 메소드를 사용하여 로그인 인텐트 실행
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }
    }
    // onActivityResult() 메소드는 startActivityForResult()로 실행한 액티비티가 종료되면 호출되는 콜백 메소드이다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // requestCode : startActivityForResult() 메소드 호출 시 보냈던 요청 코드 값
        // resultCode : setResult() 메소드로 설정한 결과 코드 값
        // data : onActivityResult() 메소드에서 결과 데이터를 가져올 때 사용할 Intent 객체

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // Google 로그인 인텐트에서 반환된 결과 처리
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            // getSignedInAccountFromIntent() 메소드를 사용하여 GoogleSignInAccount 객체 생성
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            // handleSignInResult() 메소드를 사용하여 GoogleSignInAccount 객체 처리
            handleSignInResult(task)
        }
    }
    // Google 로그인 결과 처리하는 함수
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // 로그인이 완료된 Task에서 결과값을 가져온다
            val account = completedTask.getResult(ApiException::class.java)

            // 구글 로그인 성공
            Log.w("google", "signInResult:success code=" + account)
            // 구글 로그인 UI 업데이트 함수 호출
            updateGoogleLoginUi()

        } catch (e: ApiException) {
            // 구글 로그인 실패
            Log.w("google", "signInResult:failed code=" + e.statusCode)
        }
    }


    //처음에 구조를 잘못 생각해서 오류가 났었는데,
    //구글 로그인 할때 가입하지 않았는데 없는 값을 내라는 코드가 있었는데
    //코드 알고리즘을 바뀌서 수정하였다.

    //함수는 구글 로그인 성공시 호출되어 구글 계정 정보를 가져와서 UI를 업데이트합니다.
    private fun updateGoogleLoginUi() {
        //메서드를 사용하여 마지막으로 로그인 한 구글 계정을 가져옵니다.
        val acct = GoogleSignIn.getLastSignedInAccount(App.instance)

        //구글 계정 정보를 변수에 저장합니다. (이메일, 프로필 사진, 토큰 값을 들고 옵니다.)
        if (acct != null) {
            val personName = acct?.displayName
            val personGivenName = acct?.givenName
            val personFamilyName = acct?.familyName
            val personEmail = acct?.email
            val personId = acct?.id
            val personPhoto: Uri? = acct?.photoUrl
            val personToken = acct.idToken
            val personExpire = acct?.isExpired

            //변수에 idToken 값을 저장합니다.
            userToken = acct.idToken.toString()
            Log.d("userToken google", personToken.toString() + "<<<")


            //변수에 구글 계정 이메일을 저장합니다.
            val split = personEmail!!.split("@")
            // userEmail = split[0] + "@google"
            userEmail = personEmail.toString()
            // userEmail = "123456@gmail.com"

            //변수에 구글 계정 이메일을 저장합니다.
            Log.d("userEmail", userEmail)
            userInfo.email = userEmail
            //App.sharedPrefs를 사용하여 userToken과 userEmail을 저장합니다.
            App.sharedPrefs.saveToken(userToken)
            App.sharedPrefs.saveInfo(userEmail!!, personPhoto.toString())
            //postGoogleLoginInfo() 함수를 호출하여 서버에 로그인 정보를 전송합니다.
            postGoogleLoginInfo()
        } else {
            Log.d("google", "access fail")
        }
    }

    // 사용자 정보가 저장된 Shared Preferences에서 토큰과 이메일을 가져와서 로그인 API를 호출하는 함수
    private fun postGoogleLoginInfo() {
        // 토큰과 이메일이 모두 존재하는 경우
        if (App.sharedPrefs.getToken() != null && userEmail != null) {
            // 로그인 플래그를 2로 설정하여 Google 로그인임을 표시
            App.sharedPrefs.saveFlag("2")
            // 사용자 정보 객체에 이메일을 설정
            userInfo.email = App.sharedPrefs.getEmail()
            // 사용자 토큰을 가져와서 변수에 저장
            userToken = App.sharedPrefs.getToken()!!
            // 로그인 API를 호출하고, 성공 시 데이터를 Shared Preferences에 저장
            repository.postLoginInfo(userToken, userInfo,
                success = {
                    it.run {
                        val data = it.data

                        // App.sharedPrefs.saveGoogleId(data.toString())

                        // Google ID는 현재 저장하지 않음
                        // 토큰을 Shared Preferences에
                        App.sharedPrefs.saveToken(data.toString())
                        Log.d("shared_id", App.sharedPrefs.getGoogleId().toString())
                    }
                }, fail = {
                    // 로그인 실패 시 Google ID를 null로 설정
                    App.sharedPrefs.saveGoogleId(null)
                })

            // sharedPrefs에서 이메일과 GoogleId를 가져와서 로그로 출력
            Log.d("google data1", App.sharedPrefs.getEmail().toString())
            Log.d("google data2", App.sharedPrefs.getGoogleId().toString())

            // GoogleId나 Token 값이 없을 경우 로그인이 실패한 것으로 판단하고 MainActivity로 이동
            if (App.sharedPrefs.getGoogleId() == null || App.sharedPrefs.getToken() == "") {
                val intent = Intent(App.instance, MainActivity::class.java)
                intent.putExtra("join", 1)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                App.sharedPrefs.saveFlag("2")
                startActivity(intent)
                finish()
                Log.e("LoginActivity", "postGoogleLoginInfo")
                // GoogleId와 Token 값이 모두 있을 경우 로그인에 성공한 것으로 판단하고 MainActivity로 이동
            } else {
                val intent = Intent(App.instance, MainActivity::class.java)
                intent.putExtra("join", 0)
                App.sharedPrefs.saveFlag("2")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                Log.d("fail shared id ", App.sharedPrefs.getGoogleId().toString())
                Log.d("구글 성공 !!", "구글 성공!")
            }
        }
    }


}
