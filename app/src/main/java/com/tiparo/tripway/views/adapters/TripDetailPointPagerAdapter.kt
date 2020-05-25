package com.tiparo.tripway.views.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tiparo.tripway.views.ui.PointFragment
import timber.log.Timber

class TripDetailPointPagerAdapter(fragment: Fragment, private val size: Int) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = size

    override fun createFragment(position: Int): Fragment {
        Timber.d("FragmentStateAdapter.createFragment() = $position")
        val arguments = Bundle().apply {
            putInt(PointFragment.ARG_POSITION, position)
        }
        return PointFragment().apply { this.arguments = arguments }
    }
}