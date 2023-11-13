package com.goodsoft.financeshield.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.activities.CategoryActivity
import com.goodsoft.financeshield.adapters.CategoriesAdapter
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoriesFbAdd.setOnClickListener{ startActivity(Intent(activity, CategoryActivity::class.java)) }
        binding.categoriesFbHELP.setOnClickListener{ showHelpInfo() }
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.tab_categories_title)
        refreshData()
    }

    private fun refreshData() {
        val db = DBHelper(requireActivity().applicationContext)
        val cursor = db.getCategories(false)
        if (cursor == null) return
        binding.categoriesRvCategories.layoutManager = LinearLayoutManager(context)
        binding.categoriesRvCategories.adapter = CategoriesAdapter(cursor, requireContext())
        /* val myAdapter = CategoriesAdapter(cursor)
        myAdapter.setInterface( object: CategoriesAdapter.CategoriesInterface { override fun setOnOffCategoryFun(id: Int, setToOn: Boolean) { changeOnOffCategory(id, setToOn) } })
        binding.categoriesRvCategories.adapter = myAdapter */
        /* private fun changeOnOffCategory(id: Int, setToOn: Boolean) {
        val db = DBHelper(requireContext())
        db.changeCategoryActive(id, setToOn)
        refreshData()
        } */
    }

    private fun showHelpInfo() {
        val myAlertDialog = AlertDialog.Builder(activity)
        myAlertDialog.setTitle(R.string.help_information_title)
        myAlertDialog.setMessage(R.string.CF_help_information_text)
        myAlertDialog.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
        myAlertDialog.show()
    }

}