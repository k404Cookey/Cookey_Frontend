package com.googleplay.cookey.navigation.upload

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.databinding.FilterListItemBinding


class UploadPreviewFilterAdapter(val filterList: ArrayList<RecipeDTO.Themes>) :
    RecyclerView.Adapter<UploadPreviewFilterAdapter.UploadPreviewFilterHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : UploadPreviewFilterAdapter.UploadPreviewFilterHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_list_item, parent, false)
//        return UploadPreviewFilterAdapter.UploadPreviewFilterHolder(view)
        val layoutInflater = LayoutInflater.from(parent.context)
        return UploadPreviewFilterAdapter.UploadPreviewFilterHolder(FilterListItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: UploadPreviewFilterAdapter.UploadPreviewFilterHolder, position: Int) {
        holder.name.setText(filterList[position].name)
        holder.binding.run {
            tvFilterName.setBackgroundResource(R.drawable.no_select_border_layout)
            tvFilterName.setTextColor(Color.parseColor("#FF8C4B"))
        }
    }

    class  UploadPreviewFilterHolder(val binding: FilterListItemBinding) : RecyclerView.ViewHolder(binding.root){
        val name = itemView.findViewById<TextView>(R.id.tv_filter_name)
    }

//    class UploadPreviewFilterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val name = itemView.findViewById<TextView>(R.id.tv_filter_name)
//    }

}