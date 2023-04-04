package com.googleplay.cookey.result

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.googleplay.cookey.App
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.databinding.ItemResultRecipeBinding
import com.googleplay.cookey.detail.ResultMainIngredientAdapter


class ResultViewHolder(val binding: ItemResultRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

    private val imageViewResultRecipe: ImageView = binding.ivResultImage

    private lateinit var resultMainIngredientAdapter: ResultMainIngredientAdapter

    private val tvRecipeName: TextView = binding.tvRecipeName
    private val rvMainIngredientInItem = itemView.findViewById<RecyclerView>(R.id.rv_result_main_ingredient)
    private val tvRating: TextView = binding.tvStarRating
    private val tvViewCount: TextView = binding.tvViewcount
    private val ivProfileImage: ImageView = binding.ivResultProfile
    private val tvNickname: TextView = binding.tvResultNickname
    private val tvCalendar: TextView = binding.tvCalendar


    fun bindItem(data: RecipeDTO.RecipeFinal) {
        data.thumbnail?.let {
            if (it.isNotEmpty()) {
                Glide.with(App.instance)
                    .load(it)
                    .placeholder(R.drawable.ic_face)
                    .into(imageViewResultRecipe);
            }
        }

        tvRecipeName.text = data.title
        setMainIngredientRecyclerView(data.mainIngredients)

        val floatRatingAvgRound = Math.round(data.starCount!! * 10) / 10f
        tvRating.text = floatRatingAvgRound.toString()
        tvViewCount.text = data.viewCount

        Glide.with(App.instance)
            .load(data.writer?.imageUrl)
            .placeholder(R.drawable.ic_face)
            .into(ivProfileImage);
        tvNickname.text = data.writer?.name
        tvCalendar.text = data.time
    }

    private fun setMainIngredientRecyclerView(mainIngredients: ArrayList<RecipeDTO.MainIngredients>) {
        resultMainIngredientAdapter = ResultMainIngredientAdapter()
        rvMainIngredientInItem.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        rvMainIngredientInItem.setHasFixedSize(true)
        rvMainIngredientInItem.adapter = resultMainIngredientAdapter
        resultMainIngredientAdapter.resultMainIngredients = mainIngredients
    }
}