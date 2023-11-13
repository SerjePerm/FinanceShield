package com.goodsoft.financeshield.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodsoft.financeshield.adapters.ParsersAdapter
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.databinding.ActivityParsersListBinding

class ParsersListActivity : AppCompatActivity() {
//class ParsersListActivity : AppCompatActivity(), ParsersAdapter.ParsersInterface {
    private lateinit var binding: ActivityParsersListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParsersListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeButtonsClicks()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun initializeButtonsClicks() {
        binding.parsersIvAdd.setOnClickListener { startActivity(Intent(this@ParsersListActivity, ParserActivity::class.java)) }
        binding.parsersIvBack.setOnClickListener { finish() }
    }

    private fun refreshData() {
        val db = DBHelper(this)
        val cursor = db.getParsers(false)
        if (cursor == null) return
        binding.parsersRvParsers.layoutManager = LinearLayoutManager(this)
        binding.parsersRvParsers.adapter = ParsersAdapter(cursor, this)
        /* override fun delParser(parserId: Int) {val db = DBHelper(this)
        db.delParser(parserId); refreshData() } */
    }

}