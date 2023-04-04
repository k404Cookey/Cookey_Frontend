package com.googleplay.cookey.navigation.search

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.googleplay.cookey.App
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.databinding.ItemRandomRecipeBinding

class SearchViewHolder(val binding: ItemRandomRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

    private val imageViewRandomRecipe: ImageView = binding.ivRandomRecipe

    fun bindItem(data: RecipeDTO.RecipeFinal) {
        data.thumbnail?.let {
            if (it.isNotEmpty()) {
                Glide.with(App.instance)
                    .load(data.thumbnail)
                    .placeholder(R.drawable.ic_face)
                    .into(imageViewRandomRecipe);
            }
        }
    }
}