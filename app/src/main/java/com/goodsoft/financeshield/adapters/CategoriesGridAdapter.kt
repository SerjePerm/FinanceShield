package com.goodsoft.financeshield.adapters

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.data.DBC
import com.goodsoft.financeshield.data.ImagesForCategories

class CategoriesGridAdapter(private var data: Cursor, private val myCategoryGridInterface: CategoryGridInterface) :
    RecyclerView.Adapter<CategoriesGridAdapter.MyViewHolder>() {

    interface CategoryGridInterface {
        fun setCategory(name: String)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.itemCategoryGrid_tvName)
        val ivImage: ImageView = itemView.findViewById(R.id.itemCategoryGrid_ivIcon)
    }

    override fun getItemCount(): Int {
        return data.count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myItemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category_grid, parent, false)
        return MyViewHolder(myItemView)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (data.moveToPosition(position)) {
            val tmpTitle = data.getString(data.getColumnIndex(DBC.Categs.C_NAME))
            val tmpImage = data.getInt(data.getColumnIndex(DBC.Categs.C_ICON))
            //
            holder.tvName.text = tmpTitle
            val resID: Int = ImagesForCategories.images[tmpImage]
            holder.ivImage.setImageResource(resID)
            //
            holder.itemView.setOnClickListener { myCategoryGridInterface.setCategory(tmpTitle) }
        } else Log.e(this.javaClass.name, "CategoriesGridAdapter onBindViewHolder: Error move to pos $position")
    }

}