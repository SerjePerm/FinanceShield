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
import com.goodsoft.financeshield.activities.TransactionActivity
import com.goodsoft.financeshield.adapters.TransactionsAdapter
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.databinding.FragmentTransactionsBinding

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.transactionsFbADD.setOnClickListener { startActivity(Intent(activity, TransactionActivity::class.java)) }
        binding.transactionsFbHELP.setOnClickListener{ showHelpInfo() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.tab_transactions_title)
        refreshData()
    }

    private fun showHelpInfo() {
        val myAlertDialog = AlertDialog.Builder(activity)
        myAlertDialog.setTitle(R.string.help_information_title)
        myAlertDialog.setMessage(R.string.TF_help_information_text)
        myAlertDialog.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
        myAlertDialog.show()
    }

    private fun refreshData() {
        val db = DBHelper(requireActivity().applicationContext)
        val cursor = db.getTransactions()
        if (cursor == null) return
        //
        val myAdapter = TransactionsAdapter(cursor)
        binding.transactionsRvTransactions.adapter = myAdapter
        binding.transactionsRvTransactions.layoutManager = LinearLayoutManager(context)
    }
}