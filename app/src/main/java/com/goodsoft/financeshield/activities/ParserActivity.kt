package com.goodsoft.financeshield.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.data.Parser
import com.goodsoft.financeshield.databinding.ActivityParserBinding
import com.goodsoft.financeshield.utils.boolToInt
import com.goodsoft.financeshield.utils.clearNumber
import com.goodsoft.financeshield.utils.splitStrToArray
import kotlin.math.round

class ParserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParserBinding
    private var parserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val requestIntent: Intent = intent
        parserId = requestIntent.getIntExtra("id", 0)
        //
        //load strings if add from PUSH message
        val tmpPackage = requestIntent.getStringExtra("exPackage")
        val tmpTitle = requestIntent.getStringExtra("exTitle")
        val tmpText = requestIntent.getStringExtra("exText")
        binding.parserEtPackage.setText(tmpPackage)
        binding.parserEtExampleTitle.setText(tmpTitle)
        binding.parserEtExampleText.setText(tmpText)
        //
        if (parserId > 0) initializeForEdit()
        initializeButtonsClicks()
    }

    private fun initializeButtonsClicks() {
        binding.parserBtnCancel.setOnClickListener { finish() }
        binding.parserIvDelete.setOnClickListener { deleteClick() }
        binding.parserBtnOk.setOnClickListener { okClick() }
        //Listeners to update parser result
        binding.parserEtExampleTitle.doAfterTextChanged { updateParserResult() }
        binding.parserEtExampleText.doAfterTextChanged { updateParserResult() }
        //binding.parserSpinPayee.setOnItemClickListener { updateParserResult() }
        //binding.parserSpinValue.setOnItemClickListener { updateParserResult() }
        //binding.parserSpinCard.setOnItemClickListener { updateParserResult() }
        //binding.parserSpinBalance.setOnItemClickListener { updateParserResult() }
    }

    private fun initializeForEdit() {
        val db = DBHelper(this)
        val parser = db.getParser(parserId)
        //
        if (parser.active == 0) binding.parserCbOnCategory.isChecked = false
        binding.parserEtTitle.setText(parser.title)
        binding.parserEtPackage.setText(parser.packag)
        binding.parserEtKeyword.setText(parser.keyword)
        binding.parserSpinPayee.setSelection(parser.ipayee-1)
        binding.parserSpinValue.setSelection(parser.ivalue-1)
        binding.parserSpinCard.setSelection(parser.icard-1)
        binding.parserSpinBalance.setSelection(parser.ibalance-1)
        binding.parserEtExampleTitle.setText(parser.extitle)
        binding.parserEtExampleText.setText(parser.extext)
    }

    private fun okClick() {
        // default values
        if (binding.parserEtPackage.text.isEmpty()) binding.parserEtPackage.error = ""
        if (binding.parserEtKeyword.text.isEmpty()) binding.parserEtKeyword.error = ""
        if ((binding.parserEtPackage.text.isEmpty()) || (binding.parserEtKeyword.text.isEmpty())) return
        if (binding.parserEtTitle.text.isEmpty()) binding.parserEtTitle.setText(R.string.TA_unnamed)
        // collect values
        val tmpActive = boolToInt(binding.parserCbOnCategory.isChecked)
        val tmpTitle = binding.parserEtTitle.text.toString()
        val tmpPackage = binding.parserEtPackage.text.toString()
        val tmpKeyword = binding.parserEtKeyword.text.toString()
        val tmpIPayee = binding.parserSpinPayee.selectedItem.toString().toInt()
        val tmpIValue = binding.parserSpinValue.selectedItem.toString().toInt()
        val tmpICard = binding.parserSpinCard.selectedItem.toString().toInt()
        val tmpIBalance = binding.parserSpinBalance.selectedItem.toString().toInt()
        val tmpExTitle = binding.parserEtExampleTitle.text.toString()
        val tmpExText = binding.parserEtExampleText.text.toString()
        // insert / update values
        val db = DBHelper(this)
        val myParser = Parser(parserId,tmpTitle,tmpPackage,tmpKeyword,tmpIPayee,tmpIValue,tmpICard,tmpIBalance,tmpExTitle,tmpExText, tmpActive)
        if (parserId>0) db.updateParser(myParser) else db.addParser(myParser)
        finish()
    }

    private fun deleteClick() {
        val db = DBHelper(this)
        db.delParser(parserId)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun updateParserResult() {
        //COLLECT DATA
        val iPayee = binding.parserSpinPayee.selectedItem.toString().toInt()
        val iValue = binding.parserSpinValue.selectedItem.toString().toInt()
        val iCard = binding.parserSpinCard.selectedItem.toString().toInt()
        val iBalance = binding.parserSpinBalance.selectedItem.toString().toInt()
        val tmpTitle = binding.parserEtExampleTitle.text.toString()
        val tmpText = binding.parserEtExampleText.text.toString()
        val textArr = splitStrToArray(tmpText, tmpTitle)
        val itemsCount = textArr.size
        //SHOW STR ARRAY WITH INDICES
        binding.parserTvExampleResult.text = ""
        for (i in textArr.indices) binding.parserTvExampleResult.append("[${i+1}]: <${textArr[i]}> ")
        //DEFAULT RESULTS
        var resPayee = "ошибка"; var resValue = "ошибка"; var resCard = "ошибка"; var resBalance = "ошибка"
        //GET RESULT FROM TEXT
        if (itemsCount >= iPayee) resPayee = textArr[iPayee - 1]
        if (itemsCount >= iValue) resValue = textArr[iValue - 1]
        if (itemsCount >= iCard) resCard = textArr[iCard - 1]
        if (itemsCount >= iBalance) resBalance = textArr[iBalance - 1]
        //CLEAR VALUES
        resValue = clearNumber(resValue)
        resCard = clearNumber(resCard)
        resBalance = clearNumber(resBalance)
        if (resCard == "") resCard = "номер не найден -> 0"
        val resValueF = round(resValue.toFloat() * 100) / 100
        val tmpBalance = round(resBalance.toFloat() * 100) / 100
        //SHOW VALUES
        binding.parserTvPayeeResult.text = " -> $resPayee"
        binding.parserTvValueResult.text = " -> $resValueF"
        binding.parserTvCardResult.text = " -> $resCard"
        binding.parserTvBalanceResult.text = " -> $tmpBalance"
    }
}