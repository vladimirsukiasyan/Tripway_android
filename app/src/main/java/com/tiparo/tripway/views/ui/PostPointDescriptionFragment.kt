package com.tiparo.tripway.views.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentPostPointDescriptionBinding
import com.tiparo.tripway.utils.EventObserver
import com.tiparo.tripway.utils.setupSnackbar
import com.tiparo.tripway.viewmodels.PostPointViewModel
import javax.inject.Inject


class PostPointDescriptionFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel: PostPointViewModel by navGraphViewModels(R.id.postPointGraph) {
        viewModelFactory
    }

    private lateinit var binding: FragmentPostPointDescriptionBinding
    // This property is only valid between onCreateView and
    // onDestroyView.

    val KEY_DESCRIPTION = "describePointEditText"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_post_point_description,
            container,
            false
        )
        binding.viewmodel = viewModel

        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner

        savedInstanceState?.let{
            binding.describePointEditText.setText(it.getString(KEY_DESCRIPTION,""))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSnackbar()
        setupNavigation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_DESCRIPTION, binding.describePointEditText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.pointSaved.observe(this, EventObserver {
            //TODO показывать юзеру в посылаемом фрагмент snackbar, что поинт сохраняется в фоновом режиме
            val direction = PostPointDescriptionFragmentDirections.actionPostPointDescriptionFragmentDestToHomeFragmentDest(it)
            findNavController().navigate(direction)
        })
    }
}