package com.tiparo.tripway.views.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentPointBinding
import com.tiparo.tripway.viewmodels.TripDetailViewModel
import com.tiparo.tripway.views.adapters.PointPhotosAdapter
import com.tiparo.tripway.views.common.ImagesGridItemDecorator
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class PointFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TripDetailViewModel by viewModels(
        { requireParentFragment() },
        { viewModelFactory }
    )

    private lateinit var binding: FragmentPointBinding
    private lateinit var photosAdapter: PointPhotosAdapter

    var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_point,
            container,
            false
        )
        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pointName = viewModel.getPointName(position)
        binding.pointDescription = viewModel.getPointDescription(position)
        initRecycleView()
    }

    private fun initRecycleView() {
        //TODO грузить в начале placeholder, создавая при этом нужно количество item в recycleView
        photosAdapter = PointPhotosAdapter { uri ->
            val imageDialog = ImageViewerDialogFragment.newInstance(uri)
            imageDialog.show(parentFragmentManager, ImageViewerDialogFragment.TAG_FRAGMENT)
//            requireActivity().supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.nav_host_fragment_container,imageDialog)
//                .addToBackStack(ImageViewerDialogFragment.TAG_FRAGMENT)
//                .commit()
        }
        with(binding.photosGrid) {
            adapter = photosAdapter
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
            addItemDecoration(ImagesGridItemDecorator(resources.getDimensionPixelSize(R.dimen.images_grid_spacing)))
        }
        //subscribe on receiving pointPhotos from viewModel
        viewModel.getPointPhotos(position).observe(viewLifecycleOwner) {
            photosAdapter.photosUriList = it
            photosAdapter.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val ARG_POSITION = "position"

        const val SPAN_COUNT = 4
    }
}