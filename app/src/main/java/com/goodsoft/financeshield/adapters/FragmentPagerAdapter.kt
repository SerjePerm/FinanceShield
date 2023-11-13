package com.goodsoft.financeshield.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goodsoft.financeshield.fragments.CategoriesFragment
import com.goodsoft.financeshield.fragments.HomeFragment
import com.goodsoft.financeshield.fragments.SettingsFragment
import com.goodsoft.financeshield.fragments.TransactionsFragment

class FragmentPagerAdapter(fragmentActivityParam: FragmentActivity) : FragmentStateAdapter(fragmentActivityParam) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> TransactionsFragment()
            2 -> CategoriesFragment()
            else -> {
                SettingsFragment()
            }
        }
    }
}