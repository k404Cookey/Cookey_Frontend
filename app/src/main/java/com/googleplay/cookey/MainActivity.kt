package com.googleplay.cookey

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.googleplay.cookey.base.BaseActivity
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.ActivityMainBinding
import com.googleplay.cookey.navigation.feed.FeedFragment
import com.googleplay.cookey.navigation.home.HomeFragment
import com.googleplay.cookey.navigation.mypage.MyPageFragment
import com.googleplay.cookey.navigation.search.SearchFragment
import com.googleplay.cookey.navigation.upload.UploadActivity

//import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(R.layout.activity_main) {

    private val homeFragment by lazy { HomeFragment() }
    private val feedFragment by lazy { FeedFragment() }
    private val searchFragment by lazy { SearchFragment() }
    private val myPageFragment by lazy { MyPageFragment() }

    private var joinInfo = RecipeDTO.RequestJoin("", "", "")
    private var flag = 1
    private var cancel = "0"
    private val repository = Repository()
    private var token = App.sharedPrefs.getToken()
    private var isFirst = true

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLoginInfo()

        aleadyJoin()

        if(flag == 1 && cancel.equals("0") || isFirst) {
            isFirst = false
            showNicknameDialog()
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()
    }

    override fun onStart() {
        super.onStart()

        aleadyJoin()
    }
    fun getLoginInfo(){
        if(intent.hasExtra("join")) {
            flag = intent.getIntExtra("join", -1)
            Log.d("flag", flag.toString())
        }
        if(intent.hasExtra("cancel")) {
            cancel = intent.getStringExtra("cancel")!!
            Log.d("cancel", cancel + "fsdfsdfsd")
        }
    }
    fun aleadyJoin() {
        if(App.sharedPrefs.getName() != null) {
           isFirst = false
        } else {
            isFirst = true
        }
    }
    fun showNicknameDialog() {
        if( cancel.equals("10001")) {
            Toast.makeText(this, "레시피가 등록되었습니다!", Toast.LENGTH_SHORT).show()
        } else {

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            val edialog = LayoutInflater.from(this)
            val mView = edialog.inflate(R.layout.dialog_nickname, null)
            val edtUserNickname = mView.findViewById<EditText>(R.id.et_user_nickname)
            val submitButton = mView.findViewById<TextView>(R.id.btn_user_nickname_submit)
            var name: String = ""

            dialog.setContentView(mView)
            submitButton.setOnClickListener {
                name = edtUserNickname.text.toString()
                if(name.length >= 1) {
                    App.sharedPrefs.saveName(name)
                    postJoinInfo()
                    dialog.cancel()
                    //Toast.makeText(App.instance, "누름", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(App.instance, "한 글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            edtUserNickname.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    App.sharedPrefs.saveName(name)
                    postJoinInfo()
                    dialog.cancel()
                }
                true// false?
            })

            dialog.create()
            dialog.show()
        }
    }

    fun postJoinInfo(){
        if (App.sharedPrefs.getToken() != null && App.sharedPrefs.getEmail() != null) {
            joinInfo.email = App.sharedPrefs.getEmail()
            joinInfo.imageUrl = App.sharedPrefs.getImage()
            joinInfo.name = App.sharedPrefs.getName()

            repository.postJoinInfo(token!!, joinInfo,
                success = {
                    it.run {
                        val data= it.data
                        App.sharedPrefs.saveGoogleId(data!!)
//                        if(App.sharedPrefs.getFlag() == "1") {
//                            App.sharedPrefs.saveKakaoId(data!!)
//                            Log.d("join id", App.sharedPrefs.getKakaoId().toString())
//                        } else {
//                            App.sharedPrefs.saveGoogleId(data!!)
//                        }
                    }

                    Log.d("success", "success")
                }, fail = {
                    Log.e("MainActivty", "postJoinInfo")
                })
        }
    }

    /**
     *  <p> 하단 네비게이션 이벤트 처리 함수 </p>
     *
     */
    private fun initBottomNavigation() {

        binding.bnvHome.background = null
        binding.bnvHome.menu.getItem(2).isEnabled = false
        binding.bnvHome.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        setFragment(homeFragment)
                    }
                    R.id.feed -> {
                        setFragment(feedFragment)
                    }
                    R.id.search -> {
                        setFragment(searchFragment)
                    }
                    R.id.myPage -> {
                        setFragment(myPageFragment)
                    }
                }
                true
            }
            selectedItemId = R.id.home // 초기 프래그먼트
        }

        binding.fabWrite.setOnClickListener {
            val intent = Intent(App.instance, UploadActivity::class.java)
            startActivity(intent)
        }

    }
    /**
     *  <p> 프래그먼트 전환 함수 </p>
     *
     * @param fragment: Fragment 전환될 프래그먼트
     */
    private fun setFragment(fragment: Fragment) {

        val manager: FragmentManager = supportFragmentManager
        manager.beginTransaction().setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
            .replace(R.id.fl_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}