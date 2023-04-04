package com.googleplay.cookey.navigation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO


class ThemesAdapter(private var ThemesList: ArrayList<RecipeDTO.Themes>) :
    RecyclerView.Adapter<ThemesAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.home_theme_item ,parent, false))
    }

    override fun getItemCount(): Int {
        return ThemesList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.themes?.text = ThemesList[position].name
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val themes = itemView?.findViewById<TextView>(R.id.tv_home_theme)
    }
}