package com.goodsoft.financeshield

import android.app.ActivityManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.goodsoft.financeshield.adapters.FragmentPagerAdapter
import com.goodsoft.financeshield.data.Category
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.data.DefaultCategories
import com.goodsoft.financeshield.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var myBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.main_ToolBar))
        initializeViewPager()
        initializeFirstRun()
        initializeNLSAccess()
        createNotificationChannel()
    }

    private fun initializeViewPager() {
        binding.mainViewPager.adapter = FragmentPagerAdapter(this)
        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) { tab, pos ->
            when (pos) {
                0 -> tab.setIcon(R.drawable.ic_tab_home)
                1 -> tab.setIcon(R.drawable.ic_tab_transactions)
                2 -> tab.setIcon(R.drawable.ic_tab_categories)
                3 -> tab.setIcon(R.drawable.ic_tab_settings)
            }
        }.attach()
    }

    private fun initializeFirstRun() {
        val settings = getSharedPreferences(R.string.app_name.toString(), 0)
        if (settings.getBoolean("first_time_run", true)) {
            val db = DBHelper(this)
            db.addCategory(Category(0, "Зарплата", 19, 0, 1))
            for ((key, value) in DefaultCategories.categories) db.addCategory(Category(0, key, value, 1, 1))
        }
        settings.edit().putBoolean("first_time_run", false).apply()
    }

    private fun initializeNLSAccess() {
        //TODO string resources!
        //TODO check settings, if OFF -> break/return
        if (!checkNLSAccess()) {
            val myAlertDialog = AlertDialog.Builder(this)
            myAlertDialog.setTitle("NLS")
            myAlertDialog.setMessage("NLS denied, please allow!")
            myAlertDialog.setPositiveButton(android.R.string.ok) { _, _ -> startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }
            myAlertDialog.show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("FinanceShieldChannelId","FinanceShield channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel for FinanceShield"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(title: String, text: String) {
        val builder = NotificationCompat.Builder(this, "FinanceShieldChannelId")
        builder.setContentTitle(title).setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_app_logo)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(102, builder.build())
    }

    fun checkNLSAccess(): Boolean { return NotificationManagerCompat.getEnabledListenerPackages(this).contains(this.packageName) }

    fun checkNLSStatus(): Boolean {
        val myActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val myMap = myActivityManager.getRunningServices(Integer.MAX_VALUE).map { it.service.className }
        return myMap.contains("com.goodsoft.financeshield.services.NLS")
    }

}