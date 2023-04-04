package com.googleplay.cookey.navigation.quote

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.R
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.databinding.FilterListItemBinding

class QuoteFilterAdapter(val filterList: ArrayList<RecipeDTO.Themes>) :
    RecyclerView.Adapter<QuoteFilterAdapter.QuoteFilterHolder>() {

    var tags = ArrayList<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuoteFilterAdapter.QuoteFilterHolder {

//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.filter_list_item, parent, false)
////        return QuoteFilterAdapter.QuoteFilterHolder(view)
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuoteFilterAdapter.QuoteFilterHolder(FilterListItemBinding.inflate(layoutInflater))

    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: QuoteFilterAdapter.QuoteFilterHolder, position: Int) {
        holder.name.text = filterList[position].name
        holder.binding.run {
            tvFilterName.setTextColor(Color.parseColor("#FF8C4B"))
            tvFilterName.setBackgroundResource(R.drawable.select_border_layout)
        }

//        holder.name.text = filterList[position].name
//        holder.itemView.tv_filter_name.setTextColor(Color.parseColor("#FF8C4B"))
//        holder.itemView.tv_filter_name.setBackgroundResource(R.drawable.select_border_layout)
    }

    class QuoteFilterHolder(val binding: FilterListItemBinding) : RecyclerView.ViewHolder(binding.root){
        val name = itemView.findViewById<TextView>(R.id.tv_filter_name)
    }
//    class QuoteFilterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val name = itemView.findViewById<TextView>(R.id.tv_filter_name)
//    }
}