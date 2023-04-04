package com.googleplay.cookey.navigation.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.App
import com.googleplay.cookey.R
import com.googleplay.cookey.SharedPreferenceUtil
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.FragmentFeedBinding
import com.googleplay.cookey.detail.DetailFragment


class FeedFragment : Fragment(), FeedRecyclerInterface {

    lateinit var binding: FragmentFeedBinding

    private lateinit var v: View
    private var feedRecipeList =  ArrayList<RecipeDTO.RecipeFinal>()
    private lateinit var myAdapter: FeedRecyclerAdapter
    private val repository = Repository()
    private lateinit var rvFeed : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFeedBinding.inflate(layoutInflater)

        v = inflater.inflate(R.layout.fragment_feed, container, false)

        requsetRecipie()
        return v
    }

    private fun requsetRecipie() {
        feedRecipeList.clear()

        myAdapter = FeedRecyclerAdapter(this)
        rvFeed = v.findViewById(R.id.rv_list) as RecyclerView
        myAdapter.notifyDataSetChanged()

        repository.getFollowingFeeds(
            success = {
                it.run {
                    val data = it.list
                    feedRecipeList.addAll(data!!)
                    myAdapter.feedUpdateList(feedRecipeList)
                    rvFeed.adapter = myAdapter
                }
            },
            fail = {
                Log.d("fail", "failfailfail")
            },
            token = SharedPreferenceUtil(App.instance).getToken().toString()
        )
    }

    /** 게시글을 클릭했을 때 상제페이지로 이동 **/
    override fun onItemClicked(position: Int) {
        Log.d("로그", "TimeLinFragment - 클릭됨")
        Toast.makeText(
            App.instance,
            "상세 값 : ${this.feedRecipeList[position].id}",
            Toast.LENGTH_SHORT
        ).show()

        val activity = v.context as AppCompatActivity
        val detailFragment: Fragment = DetailFragment()

        val args = Bundle()// 클릭된 Recipe의 id 전달
        args.putInt("recipeId",feedRecipeList[position].id)

        detailFragment.arguments = args

        val manager: FragmentManager = activity.supportFragmentManager
        manager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            .setReorderingAllowed(true)
            .replace(R.id.fl_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}