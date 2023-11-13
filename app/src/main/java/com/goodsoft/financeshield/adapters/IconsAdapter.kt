package com.goodsoft.financeshield.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.data.ImagesForCategories

class IconsAdapter(private var data: Array<Int>, private val myIconsAdapterInterface: IconsAdapterInterface) : RecyclerView.Adapter<IconsAdapter.MyViewHolder>() {

    interface IconsAdapterInterface {
        fun setIcon(iconIndex: Int)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.itemCategoryGrid_ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myItemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category_grid, parent, false)
        return MyViewHolder(myItemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //val tmpIcon = position
        val resID: Int = ImagesForCategories.images[position]
        holder.ivImage.setImageResource(resID)
        holder.itemView.setOnClickListener { myIconsAdapterInterface.setIcon(position) }
    }
}