package com.goodsoft.financeshield.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.adapters.IconsAdapter
import com.goodsoft.financeshield.data.Category
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.data.ImagesForCategories
import com.goodsoft.financeshield.databinding.ActivityCategoryBinding
import com.goodsoft.financeshield.utils.boolToInt

class CategoryActivity : AppCompatActivity(), IconsAdapter.IconsAdapterInterface {

    private lateinit var binding: ActivityCategoryBinding
    private var categoryId: Int = 0
    private var selectedIcon: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeButtonsClicks()
        initializeIconsRecyclerView()
        val requestIntent: Intent = intent
        categoryId = requestIntent.getIntExtra("id", 0)
        if (categoryId > 0) initializeForEdit()
    }

    private fun initializeButtonsClicks() {
        binding.categoryBtnOk.setOnClickListener { okClick() }
        binding.categoryBtnCancel.setOnClickListener { finish() }
        binding.categoryBtnDelete.setOnClickListener { deleteClick() }
    }

    private fun initializeIconsRecyclerView() {
        var tmpArr: Array<Int> = arrayOf()
        for (i in ImagesForCategories.images.indices) tmpArr += i
        binding.categoryRvCategoriesGrid.layoutManager = GridLayoutManager(this, 3)
        binding.categoryRvCategoriesGrid.adapter = IconsAdapter(tmpArr, this)
    }

    private fun initializeForEdit() {
        binding.categoryBtnDelete.visibility = View.VISIBLE
        val db = DBHelper(this)
        val category = db.getCategory(categoryId)
        binding.categoryEtCategoryName.setText(category.name)
        if (category.active == 0) binding.categoryCbOnCategory.isChecked = false
        if (category.expense == 0) binding.categorySExpense.isChecked = false
        selectedIcon = category.icon
        val resID: Int = ImagesForCategories.images[category.icon]
        binding.categoryIvSelIconImage.setImageResource(resID)
    }

    private fun okClick() {
        if (binding.categoryEtCategoryName.text.isEmpty()) binding.categoryEtCategoryName.setText(R.string.TA_unnamed)
        //
        val tmpName = binding.categoryEtCategoryName.text.toString()
        val tmpExpense = binding.categorySExpense.isChecked
        val tmpActive = boolToInt(binding.categoryCbOnCategory.isChecked)
        //
        val myCategory = Category(categoryId, tmpName, selectedIcon, boolToInt(tmpExpense), tmpActive)
        val db = DBHelper(this)
        if (categoryId > 0) db.updateCategory(myCategory) else db.addCategory(myCategory)
        finish()
    }

    private fun deleteClick() {
        val db = DBHelper(this)
        db.delCategory(categoryId)
        finish()
    }

    override fun setIcon(iconIndex: Int) {
        selectedIcon = iconIndex
        val resID: Int = ImagesForCategories.images[iconIndex]
        binding.categoryIvSelIconImage.setImageResource(resID)
    }
}