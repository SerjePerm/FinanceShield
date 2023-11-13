package com.goodsoft.financeshield.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.activities.TransactionActivity
import com.goodsoft.financeshield.data.DBC
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.data.ImagesForCategories
import com.goodsoft.financeshield.utils.extractDateFromSQLite
import com.goodsoft.financeshield.utils.extractTimeFromSQLite
import com.goodsoft.financeshield.utils.floatToFormattedString
import java.util.*

class TransactionsAdapter(private val data: Cursor) : RecyclerView.Adapter<TransactionsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.itemTransaction_tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.itemTransaction_tvTime)
        val tvTitle: TextView = itemView.findViewById(R.id.itemTransaction_tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.itemTransaction_tvCategory)
        val tvValue: TextView = itemView.findViewById(R.id.itemTransaction_tvValue)
        val tvCard: TextView = itemView.findViewById(R.id.itemTransaction_tvCard)
        val vIndicator: View = itemView.findViewById(R.id.itemTransaction_vIndicator)
        val ivIcon: ImageView = itemView.findViewById(R.id.itemTransaction_ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (data.moveToPosition(position)) {
            val tmpId = data.getInt(data.getColumnIndex(DBC.Trans.C_ID)) //important, not allow direct to setOnCLickListener!
            val tmpValue = data.getFloat(data.getColumnIndex(DBC.Trans.C_VALUE))
            val tmpCategory = data.getString(data.getColumnIndex(DBC.Trans.C_CATEGORY))
            //
            holder.tvTitle.text = data.getString(data.getColumnIndex(DBC.Trans.C_TITLE))
            holder.tvDate.text = extractDateFromSQLite(data.getString(data.getColumnIndex(DBC.Trans.C_DATE)))
            holder.tvTime.text = extractTimeFromSQLite(data.getString(data.getColumnIndex(DBC.Trans.C_DATE)))
            holder.tvCard.text = data.getString(data.getColumnIndex(DBC.Trans.C_CARD))
            holder.tvCategory.text = tmpCategory
            holder.tvValue.text = floatToFormattedString(tmpValue)
            //
            if (tmpValue >= 0) holder.vIndicator.setBackgroundResource(R.color.income)
            if (tmpCategory == "") holder.vIndicator.setBackgroundResource(R.color.indian_red)
            //
            val db = DBHelper(holder.itemView.context)
            val resID: Int = ImagesForCategories.images[db.getCategoryIcon(tmpCategory)]
            holder.ivIcon.setImageResource(resID)
            //
            holder.itemView.setOnClickListener {
                val myIntent = Intent(holder.itemView.context, TransactionActivity::class.java)
                myIntent.putExtra("id", tmpId)
                startActivity(holder.itemView.context, myIntent, null)
            }
        } else Log.e(this.javaClass.name, "TransactionsAdapter onBindViewHolder: Error move to pos $position")
    }



}