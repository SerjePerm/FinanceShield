package com.goodsoft.financeshield.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.activities.ParserActivity
import com.goodsoft.financeshield.data.DBC
import com.goodsoft.financeshield.data.DBHelper

class ParsersAdapter(private var data: Cursor, private var myContext: Context) : RecyclerView.Adapter<ParsersAdapter.MyViewHolder>() {
//class ParsersAdapter(private var data: Cursor, private var myParsersInterface: ParsersInterface) : RecyclerView.Adapter<ParsersAdapter.MyViewHolder>() {
    //interface ParsersInterface { fun delParser(parserId: Int) }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.itemTwo_item1)
        val tvKeyword: TextView = itemView.findViewById(R.id.itemTwo_item2)
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
            val tmpId = data.getInt(data.getColumnIndex(DBC.Parsers.C_ID))
            val tmpActive = data.getInt(data.getColumnIndex(DBC.Parsers.C_ACTIVE))
            if (tmpActive == 0) holder.tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTitle.text = data.getString(data.getColumnIndex(DBC.Parsers.C_TITLE))
            holder.tvKeyword.text = data.getString(data.getColumnIndex(DBC.Parsers.C_KEYWORD))
            //
            holder.ivDelete.setOnClickListener{
                //myParsersInterface?.delParser(tmpId)
                val db = DBHelper(myContext)
                db.delParser(tmpId)
                val tmpCursor = db.getParsers(false)
                if (tmpCursor != null) data = tmpCursor
                this.notifyItemRemoved(position)  //this.notifyDataSetChanged()
            }
            holder.itemView.setOnClickListener {
                val myIntent = Intent(holder.itemView.context, ParserActivity::class.java)
                myIntent.putExtra("id", tmpId)
                ActivityCompat.startActivity(holder.itemView.context, myIntent, null)
            }
        } else Log.e(this.javaClass.name, "ParsersAdapter onBindViewHolder: Error move to pos $position")
    }

}