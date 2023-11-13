package com.goodsoft.financeshield.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.adapters.CardsAdapter
import com.goodsoft.financeshield.data.DBHelper
import com.goodsoft.financeshield.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeButtonsClicks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.tab_home_title)
        refreshData()
    }

    private fun initializeButtonsClicks() {
        binding.homeClCardsHeader.setOnClickListener{ showHideCards() }
        binding.homeClDayHeader.setOnClickListener{ showHideToday() }
        binding.homeClMonthHeader.setOnClickListener{ showHideMonth() }
    }

    private fun showHideCards() {
        if (binding.homeRvCards.visibility == View.GONE) binding.homeRvCards.visibility = View.VISIBLE
        else binding.homeRvCards.visibility = View.GONE
    }

    private fun showHideToday() {
        if (binding.homeExpandableTodayLayout.visibility == View.GONE) binding.homeExpandableTodayLayout.visibility = View.VISIBLE
        else binding.homeExpandableTodayLayout.visibility = View.GONE
    }

    private fun showHideMonth() {
        if (binding.homeExpandableMonthLayout.visibility == View.GONE) binding.homeExpandableMonthLayout.visibility = View.VISIBLE
        else binding.homeExpandableMonthLayout.visibility = View.GONE
    }

    private fun refreshData() {
        val db = DBHelper(requireActivity().applicationContext)
        val cursor = db.getCards()
        if (cursor == null) return
        binding.homeRvCards.layoutManager = LinearLayoutManager(context)
        binding.homeRvCards.adapter = CardsAdapter(cursor, requireContext())
        /* val myAdapter = CardsAdapter(cursor)
        myAdapter.setInterface(object : CardsAdapter.CardsInterface{ override fun delCard(card: Int) { delCardExec(card) } } )
        binding.homeRvCards.adapter = myAdapter */
        /* private fun delCardExec(card: Int) { val db = DBHelper(requireActivity().applicationContext)
        db.delCard(card); refreshData() } */
    }

 }