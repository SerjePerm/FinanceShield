package com.goodsoft.financeshield.adapters

import android.annotation.SuppressLint
import android.content.Context
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
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.utils.floatToFormattedString

class CardsAdapter(private var data: Cursor, private var myContext: Context) : RecyclerView.Adapter<CardsAdapter.MyViewHolder>() {

    /* private var myCardsInterface: CardsInterface? = null
    interface CardsInterface { fun delCard(card: Int) }
    fun setInterface(interfaceParam: CardsInterface) { this.myCardsInterface = interfaceParam } */

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCard: TextView = itemView.findViewById(R.id.itemTwo_item1)
        val tvValue: TextView = itemView.findViewById(R.id.itemTwo_item2)
        val ivDelete: ImageView = itemView.findViewById(R.id.itemTwo_ivDelete)
    }

    override fun getItemCount(): Int {
        return data.count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myItemView = LayoutInflater.from(parent.context).inflate(R.layout.item_two_text, parent, false)
        return MyViewHolder(myItemView)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (data.moveToPosition(position)) {
            val tmpCard = data.getInt(data.getColumnIndex(DBC.Cards.C_CARD))
            val tmpValue = data.getFloat(data.getColumnIndex(DBC.Cards.C_VALUE))
            holder.tvCard.text = tmpCard.toString()
            holder.tvValue.text = floatToFormattedString(tmpValue)
            //holder.ivDelete.setOnClickListener{ myCardsInterface?.delCard(tmpCard) }
            holder.ivDelete.setOnClickListener{
                val db = DBHelper(myContext)
                db.delCard(tmpCard)
                val tmpCursor = db.getCards()
                if (tmpCursor != null) data = tmpCursor
                this.notifyItemRemoved(position)  //this.notifyDataSetChanged()
            }
        } else Log.e(this.javaClass.name, "CardsAdapter onBindViewHolder: Error move to pos $position")

    }
}