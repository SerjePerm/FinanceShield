package com.goodsoft.financeshield.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodsoft.financeshield.adapters.NotificationsAdapter
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.databinding.ActivityNotificationsListBinding

class NotificationsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeButtonsClicks()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun initializeButtonsClicks() {
        binding.notificationsIvBack.setOnClickListener { finish() }
        binding.notificationsIvHelp.setOnClickListener { showHelp() }
    }

    private fun refreshData() {
        val db = DBHelper(this)
        val cursor = db.getNotifications() ?: return //elvis if (cursor == null) return
        binding.notificationsRvNotifications.layoutManager = LinearLayoutManager(this)
        binding.notificationsRvNotifications.adapter = NotificationsAdapter(cursor)
    }

    private fun showHelp() {
        //TODO
    }

}