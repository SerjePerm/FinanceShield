package com.goodsoft.financeshield.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.data.DBC
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.data.NotificationDB
import com.goodsoft.financeshield.data.PushKeywords.pushKeywords
import com.goodsoft.financeshield.data.Transaction
import com.goodsoft.financeshield.utils.clearNumber
import com.goodsoft.financeshield.utils.splitStrToArray
import java.util.*
import kotlin.math.round

class NLS : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        //Log.e(this.javaClass.name, "NLS Created")
        //createNotificationChannel()
    }

    @SuppressLint("Range")
    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        //CHECK MAIN SWITCH
        val settings = getSharedPreferences(R.string.app_name.toString(), 0)
        if (!settings.getBoolean("PUSH auto analyze", true)) return
        //COLLECT DATA
        val tmpPackage = sbn?.packageName
        val tmpTitle = sbn?.notification?.extras?.getString(Notification.EXTRA_TITLE)
        var tmpText = sbn?.notification?.extras?.getString(Notification.EXTRA_TEXT)
        val tmpDateTime = Calendar.getInstance().time
        //CHECK 1.EMPTY MSG; 2.DUPLICATE PUSH MSG; 3.KEYWORDS IN MSG
        if (tmpText!!.isEmpty()) return
        if (settings.getString("last PUSH notification", "") == tmpText) return else settings.edit().putString("last PUSH notification", tmpText).apply()
        if (!checkPushKeywords(tmpText)) return
        //replace "nbsp" -> "_"
        for (i in tmpText!!.indices) if (tmpText[i].code == 160) tmpText = tmpText.substring(0, i) + "_" + tmpText.substring(i + 1)
        //ADD TO NOTIFICATION LOG
        val tmpId = settings.getInt("NotificationLogId", 1)
        val myNotification = NotificationDB(tmpId, tmpPackage!!, tmpTitle!!, tmpText, tmpDateTime)
        val db = DBHelper(this)
        db.addOrUpdateNotification(myNotification)
        if (tmpId == 15) settings.edit().putInt("NotificationLogId", 1).apply() else settings.edit().putInt("NotificationLogId", tmpId + 1).apply()
        //------------------ANALYZE BY PARSERS------------------
        val textArr = splitStrToArray(tmpText, tmpTitle)
        val itemsCount = textArr.size
        val cursor = db.getParsers(true) ?: return // if (cursor == null) return
        while (cursor.moveToNext()) { //FOR EACH PARSER
            val parserPackage = cursor.getString(cursor.getColumnIndex(DBC.Parsers.C_PACKAGE))
            val parserKeyword = cursor.getString(cursor.getColumnIndex(DBC.Parsers.C_KEYWORD))
            //CHECK PACKAGE AND KEYWORD
            if ((tmpPackage == parserPackage) && (tmpText.contains(parserKeyword, true))) {
                //COLLECT PARSER DATA
                val iPayee = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_IPAYEE))
                val iValue = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_IVALUE))
                val iCard = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_ICARD))
                val iBalance = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_IBALANCE))
                //DEFAULT RESULTS
                var resPayee = "ошибка";  var resValue = "ошибка"; var resCard = "ошибка"; var resBalance = "ошибка"
                //GET RESULT FROM TEXT
                if (itemsCount >= iPayee) resPayee = textArr[iPayee - 1]
                if (itemsCount >= iValue) resValue = textArr[iValue - 1]
                if (itemsCount >= iCard) resCard = textArr[iCard - 1]
                if (itemsCount >= iBalance) resBalance = textArr[iBalance - 1]
                //CLEAR VALUES
                resValue = clearNumber(resValue)
                resCard = clearNumber(resCard)
                resBalance = clearNumber(resBalance)
                //TODO IF EXPENSE 0-value else value
                var resValueF = round(resValue.toFloat() * 100) / 100
                resValueF = 0 - resValueF
                if (resCard == "") resCard = "0"
                //ADD TRANSACTION
                if ((resValue != "ошибка") && (resValue.isNotEmpty())) {
                    val myTransaction = Transaction(0, resPayee, tmpDateTime, resCard.toInt(), "", resValueF)
                    db.addTransaction(myTransaction)
                    //TODO LINKS ANALYZE
                    //TODO CREATE NOTIFICATION ABOUT ANALYZED
                }
                val tmpBalance = round(resBalance.toFloat() * 100) / 100
                if ((resCard != "ошибка") && (resBalance != "ошибка") && (resBalance.isNotEmpty())) db.addOrUpdateCard(resCard.toInt(), tmpBalance)
            }
        }
    }

    private fun checkPushKeywords(text: String): Boolean {
        for (item in pushKeywords) if (text.contains(item)) return true
        return false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("FinanceShieldChannelId", "FinanceShield channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel for FinanceShield"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, text: String) {
        val builder = NotificationCompat.Builder(this, "FinanceShieldChannelId")
        builder.setContentTitle(title).setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_app_logo)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(101, builder.build())
    }

}