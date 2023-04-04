package com.googleplay.cookey.navigation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.databinding.ItemRandomRecipeBinding
import com.googleplay.cookey.detail.DetailFragment


class SearchAdapter : RecyclerView.Adapter<SearchViewHolder>() {

    lateinit var binding: ItemRandomRecipeBinding

    private lateinit var view: View

    var randomRecipes = ArrayList<RecipeDTO.RecipeFinal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {

        view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_random_recipe,
                parent,
                false
            )

        val layoutInflater = LayoutInflater.from(parent.context)

        binding = ItemRandomRecipeBinding.inflate(layoutInflater)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val data = randomRecipes.get(position)
        holder.bindItem(data)

        holder.itemView.setOnClickListener {

            //val itemImageView = view.findViewById<ImageView>(R.id.iv_random_recipe)
            val itemImageView = binding.ivRandomRecipe
            ViewCompat.setTransitionName(itemImageView, "@string/transition_random_to_detail")

            val args = Bundle()// 클릭된 Recipe의 id 전달
            args.putInt("recipeId", data.id)

            val detailFragment: Fragment = DetailFragment()
            detailFragment.arguments = args

            val activity = view.context as AppCompatActivity
            val manager: FragmentManager = activity.supportFragmentManager
            manager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out) //프래그먼트 전환 애니메이션
                .setReorderingAllowed(true)
                .addSharedElement(itemImageView, "@string/transition_random_to_detail")
                .replace(R.id.fl_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return randomRecipes.size
    }


    fun updateRandomRecipeList(timeLines: ArrayList<RecipeDTO.RecipeFinal>) {
        this.randomRecipes.addAll(timeLines)
    }

}