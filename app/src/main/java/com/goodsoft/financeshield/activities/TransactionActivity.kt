package com.goodsoft.financeshield.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.adapters.CategoriesGridAdapter
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.data.Transaction
import com.goodsoft.financeshield.databinding.ActivityTransactionBinding
import com.goodsoft.financeshield.utils.dateToDateStr
import com.goodsoft.financeshield.utils.dateToTimeStr
import java.util.*
import kotlin.math.abs
import kotlin.math.round


class TransactionActivity : AppCompatActivity(), CategoriesGridAdapter.CategoryGridInterface {

    private lateinit var binding: ActivityTransactionBinding
    private var transactionId: Int = 0
    private var myCalendar: Calendar = Calendar.getInstance()
    private lateinit var dateListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeListener: TimePickerDialog.OnTimeSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeCategoriesRecyclerView()
        initializeDateTimeDialogs()
        initializeDesign()
        initializeButtonsClicks()
        val requestIntent: Intent = intent
        transactionId = requestIntent.getIntExtra("id", 0)
        if (transactionId > 0) initializeForEdit() else initializeForAdd()
    }

    private fun initializeDesign() {
        binding.transactionTvDate.paintFlags = 8 //Underline text
        binding.transactionTvTime.paintFlags = 8 //Underline text
        binding.transactionTvSelectedCategory.paintFlags = 8 //Underline text
    }

    private fun initializeCategoriesRecyclerView() {
        val db = DBHelper(baseContext)
        val cursor = db.getCategories(true)
        if (cursor == null) return
        binding.transactionRvCategoriesGrid.layoutManager = GridLayoutManager(this, 3)
        binding.transactionRvCategoriesGrid.adapter = CategoriesGridAdapter(cursor, this)
    }

    private fun initializeDateTimeDialogs() {
        dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(year, month, dayOfMonth)
            binding.transactionTvDate.text = dateToDateStr(myCalendar.time)
        }
        timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            binding.transactionTvTime.text = dateToTimeStr(myCalendar.time)
        }
    }

    private fun initializeForAdd() {
        binding.transactionTvDate.text = dateToDateStr(myCalendar.time)
        binding.transactionTvTime.text = dateToTimeStr(myCalendar.time)
        binding.transactionTvDate.setOnClickListener { DatePickerDialog(this@TransactionActivity, dateListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show() }
        binding.transactionTvTime.setOnClickListener { TimePickerDialog(this@TransactionActivity, timeListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show() }
        binding.transactionIvDelete.visibility = View.INVISIBLE
    }

    @Suppress("DEPRECATION")
    private fun initializeForEdit() {
        val db = DBHelper(this)
        val transaction = db.getTransaction(transactionId)
        binding.transactionEtTitle.setText(transaction.title)
        binding.transactionSExpense.isChecked = transaction.value < 0
        binding.transactionEtValue.setText(abs(transaction.value).toString())
        binding.transactionEtCard.setText(transaction.card.toString())
        binding.transactionTvDate.text = dateToDateStr(transaction.date)
        binding.transactionTvTime.text = dateToTimeStr(transaction.date)
        myCalendar.time = transaction.date
        binding.transactionTvSelectedCategory.text = transaction.category
        binding.transactionTvDate.setOnClickListener { DatePickerDialog(this@TransactionActivity, dateListener, transaction.date.year + 1900, transaction.date.month, transaction.date.date).show() }
        binding.transactionTvTime.setOnClickListener { TimePickerDialog(this@TransactionActivity, timeListener, transaction.date.hours, transaction.date.minutes, true).show() }
    }

    private fun initializeButtonsClicks() {
        binding.transactionBtnOk.setOnClickListener { okClick() }
        binding.transactionBtnCancel.setOnClickListener { finish() }
        binding.transactionIvDelete.setOnClickListener { deleteClick() }
    }

    private fun okClick() {
        // default values
        if (binding.transactionEtValue.text.isEmpty()) binding.transactionEtValue.setText("0")
        if (binding.transactionEtCard.text.isEmpty()) binding.transactionEtCard.setText("0")
        if (binding.transactionEtTitle.text.isEmpty()) binding.transactionEtTitle.setText(R.string.TA_unnamed)
        // collect values
        val tmpTitle: String = binding.transactionEtTitle.text.toString()
        val tmpDate: Date = myCalendar.time
        val tmpCard: Int = binding.transactionEtCard.text.toString().toInt()
        val tmpCategory: String = binding.transactionTvSelectedCategory.text.toString()
        var tmpValue = round(binding.transactionEtValue.text.toString().toFloat() * 100) / 100
        if (binding.transactionSExpense.isChecked) tmpValue = 0 - tmpValue
        // insert / update values
        val myTransaction = Transaction(transactionId, tmpTitle, tmpDate, tmpCard, tmpCategory, tmpValue)
        val db = DBHelper(this)
        if (transactionId > 0) db.updateTransaction(myTransaction) else db.addTransaction(myTransaction)
        finish()
    }

    private fun deleteClick() {
        val db = DBHelper(this)
        db.delTransaction(transactionId)
        finish()
    }

    override fun setCategory(name: String) {
        binding.transactionTvSelectedCategory.text = name
    }
}