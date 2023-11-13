package com.goodsoft.financeshield.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.activities.ParserActivity
import com.goodsoft.financeshield.data.DBC
import com.goodsoft.financeshield.utils.extractDateFromSQLite
import com.goodsoft.financeshield.utils.extractTimeFromSQLite

class NotificationsAdapter(private var data: Cursor) : RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPackage: TextView = itemView.findViewById(R.id.item_notification_tvPackage)
        val tvTitle: TextView = itemView.findViewById(R.id.item_notification_tvTitle)
        val tvDate: TextView = itemView.findViewById(R.id.item_notification_tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.item_notification_tvTime)
        val tvText: TextView = itemView.findViewById(R.id.item_notification_tvText)
    }

    override fun getItemCount(): Int {
        return data.count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myItemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return MyViewHolder(myItemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (data.moveToPosition(position)) {
            //val tmpId = data.getInt(data.getColumnIndex(DBC.Notifs.C_ID))
            val tmpPackage = data.getString(data.getColumnIndex(DBC.Notifs.C_PACKAGE))
            val tmpTitle = data.getString(data.getColumnIndex(DBC.Notifs.C_TITLE))
            val tmpText = data.getString(data.getColumnIndex(DBC.Notifs.C_TEXT))
            //
            holder.tvPackage.text = tmpPackage
            holder.tvTitle.text = tmpTitle
            holder.tvText.text = tmpText
            holder.tvDate.text = extractDateFromSQLite(data.getString(data.getColumnIndex(DBC.Notifs.C_DATE)))
            holder.tvTime.text = extractTimeFromSQLite(data.getString(data.getColumnIndex(DBC.Notifs.C_DATE)))
            //
            holder.itemView.setOnClickListener {
                val myIntent = Intent(holder.itemView.context, ParserActivity::class.java)
                myIntent.putExtra("exPackage", tmpPackage)
                myIntent.putExtra("exTitle", tmpTitle)
                myIntent.putExtra("exText", tmpText)
                ActivityCompat.startActivity(holder.itemView.context, myIntent, null)
            }
        } else Log.e(this.javaClass.name, "NotificationsAdapter onBindViewHolder: Error move to pos $position")
    }

}