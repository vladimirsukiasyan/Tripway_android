package com.tiparo.tripway.views.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.tiparo.tripway.databinding.FragmentPostPointListBinding
import com.tiparo.tripway.viewmodels.TripsViewModel
import javax.inject.Inject

class PostPointListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val tripsViewModel: TripsViewModel by activityViewModels {
        viewModelFactory
    }

    private var _binding: FragmentPostPointListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostPointListBinding.inflate(inflater)
        return binding.root
    }
}
