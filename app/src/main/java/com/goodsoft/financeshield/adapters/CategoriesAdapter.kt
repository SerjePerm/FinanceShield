package com.goodsoft.financeshield.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.activities.CategoryActivity
import com.goodsoft.financeshield.data.DBC
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.data.ImagesForCategories

class CategoriesAdapter (private var data: Cursor, private var myContext: Context) : RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    /* private var myCategoriesInterface: CategoriesInterface? = null
    interface CategoriesInterface {fun setOnOffCategoryFun(id: Int, setToOn: Boolean)}
    fun setInterface(interfaceParam: CategoriesInterface) {this.myCategoriesInterface = interfaceParam} */

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.itemCategory_tvName)
        val ivImage: ImageView = itemView.findViewById(R.id.itemCategory_ivIcon)
        val cbOn: CheckBox = itemView.findViewById(R.id.itemCategory_cbOnCategory)
        val tvIsExpense: TextView = itemView.findViewById(R.id.itemCategory_tvIsExpense)
    }

    override fun getItemCount(): Int {
        return data.count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myItemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return MyViewHolder(myItemView)
    }

    @SuppressLint("Range", "ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (data.moveToPosition(position)) {
            val tmpId = data.getInt(data.getColumnIndex(DBC.Categs.C_ID))
            val tmpName = data.getString(data.getColumnIndex(DBC.Categs.C_NAME))
            val tmpIcon = data.getInt(data.getColumnIndex(DBC.Categs.C_ICON))
            val tmpExpense = data.getInt(data.getColumnIndex(DBC.Categs.C_EXPENSE))
            val tmpActive = data.getInt(data.getColumnIndex(DBC.Categs.C_ACTIVE))
            //
            holder.tvTitle.text = tmpName
            val resID: Int = ImagesForCategories.images[tmpIcon]
            holder.ivImage.setImageResource(resID)
            if (tmpActive == 1) holder.cbOn.isChecked = true
            if (tmpExpense == 0) {
                holder.tvIsExpense.text = "Категория дохода"
                holder.tvIsExpense.setTextColor(Color.parseColor("#30d5c8"))
            }
            //
            holder.itemView.setOnClickListener {
                val myIntent = Intent(holder.itemView.context, CategoryActivity::class.java)
                myIntent.putExtra("id", tmpId)
                ActivityCompat.startActivity(holder.itemView.context, myIntent, null)
            }
            //
            if (tmpActive==1) holder.cbOn.setOnClickListener{
                //myCategoriesInterface?.setOnOffCategoryFun(tmpId, false)
                val db = DBHelper(myContext)
                db.changeCategoryActive(tmpId, false)
                val tmpCursor = db.getCategories(false)
                if (tmpCursor != null) data = tmpCursor
                this.notifyDataSetChanged()
            }
            else holder.cbOn.setOnClickListener{
                //myCategoriesInterface?.setOnOffCategoryFun(tmpId, true)
                val db = DBHelper(myContext)
                db.changeCategoryActive(tmpId, true)
                val tmpCursor = db.getCategories(false)
                if (tmpCursor != null) data = tmpCursor
                this.notifyDataSetChanged()
            }
        } else Log.e(this.javaClass.name, "CategoriesGridAdapter onBindViewHolder: Error move to pos $position")
    }

}