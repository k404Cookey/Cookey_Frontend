package com.googleplay.cookey.navigation.mypage

import MyPageFollowerFragment
import MyPageFollowingFragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.googleplay.cookey.App
import com.googleplay.cookey.R
import com.googleplay.cookey.databinding.ActivityMainBinding
import com.googleplay.cookey.databinding.FragmentMypageBinding
import com.googleplay.cookey.navigation.mypage.MyPagePagerAdapter
import com.googleplay.cookey.navigation.mypage.MyPageRecipeFragment
import com.googleplay.cookey.navigation.mypage.SettingActivity
import com.googleplay.cookey.SharedPreferenceUtil as SharedPreferenceUtil1


class MyPageFragment : Fragment() {

    private lateinit var v : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v =  inflater.inflate(R.layout.fragment_mypage, container, false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabs()
        settingButton()
        setUser()
    }

    private fun setUser() {
        val profile = v.findViewById<ImageView>(R.id.iv_user_profile)
        val userName = v.findViewById<TextView>(R.id.tv_user_id)
        val imageUrl = SharedPreferenceUtil1(App.instance).getImage()

        Glide.with(App.instance)
            .load(imageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_no_image)
            .into(profile)

        userName.text = SharedPreferenceUtil1(App.instance).getName()
    }

    private fun settingButton() {
        val ibSetting = v.findViewById<ImageButton>(R.id.ib_settings)
        ibSetting.setOnClickListener {
            activity?.let{
                val intent = Intent(context, SettingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initTabs() {

        val adapter by lazy { MyPagePagerAdapter(childFragmentManager) }
        val mypageViewpager = v.findViewById<ViewPager>(R.id.mypage_viewpager)
        val mypageTablayout = v.findViewById<TabLayout>(R.id.mypage_tablayout)
        adapter.addFragment(MyPageRecipeFragment(), "레시피")
        adapter.addFragment(MyPageFollowerFragment(), "팔로워")
        adapter.addFragment(MyPageFollowingFragment(), "팔로잉")
        mypageViewpager.adapter = adapter

        mypageTablayout.setupWithViewPager(mypageViewpager)
    }

}