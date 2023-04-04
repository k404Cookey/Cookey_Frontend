package com.googleplay.cookey.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO

class DetailTagAdapter : RecyclerView.Adapter<DetailTagViewHolder>() {

    private lateinit var view: View

    var tags = ArrayList<RecipeDTO.Themes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTagViewHolder {
        view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.tag_button_item,
                parent,
                false
            )
        Log.d("lsy", "${tags.toString()}")
        return DetailTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailTagViewHolder, position: Int) {
        val data = tags.get(position)
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    fun updateThemes(themes: ArrayList<RecipeDTO.Themes>) {
        tags.clear()
        tags.addAll(themes)
    }
}