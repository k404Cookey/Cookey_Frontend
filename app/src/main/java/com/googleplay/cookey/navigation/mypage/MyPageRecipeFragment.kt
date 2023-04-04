package com.googleplay.cookey.navigation.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.App
import com.googleplay.cookey.R
import com.googleplay.cookey.SharedPreferenceUtil
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.FragmentMypageRecipeBinding

class MyPageRecipeFragment : Fragment() {

    private lateinit var v: View
    private var myRecipeList = ArrayList<RecipeDTO.RecipeFinal>()
    private lateinit var rv_my_recipe : RecyclerView

    private val repository = Repository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_mypage_recipe, container, false)

        setMyRecipe()

        return v
    }

    private fun setMyRecipe() {
        val tvMyRecipieCount = v.findViewById<TextView>(R.id.tv_my_recipie_count)
        myRecipeList.clear()
        rv_my_recipe = v.findViewById(R.id.rv_my_recipe)
        rv_my_recipe.layoutManager = GridLayoutManager(App.instance, 2)

        repository.getMyRecipes(
            success = {
                it.run {
                    val data = it.list
                    myRecipeList.addAll(data!!)
                    rv_my_recipe.adapter = MyMultiViewAdapter(1,myRecipeList)
                    tvMyRecipieCount.text = "전체 ${data.size}개"
                }
            },
            fail = {
                Log.d("fail", "fail fail fail")
            },
            queryType = "my",
            token = SharedPreferenceUtil(App.instance).getToken().toString()
        )
    }

}