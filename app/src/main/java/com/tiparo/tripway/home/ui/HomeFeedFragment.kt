package com.tiparo.tripway.home.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentHomeFeedBinding
import com.tiparo.tripway.home.api.dto.HomeFeedInfo
import com.tiparo.tripway.home.ui.adapters.HomeFeedAdapter
import com.tiparo.tripway.utils.ErrorBody
import kotlinx.android.synthetic.main.fragment_home_feed.*
import javax.inject.Inject

class HomeFeedFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val vm: HomeFeedViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var adapter: HomeFeedAdapter

    private lateinit var binding: FragmentHomeFeedBinding

    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home_feed,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView()

        vm.loadFirstPageIntent()

        vm.uiStateLiveData.observe(viewLifecycleOwner, Observer { state: HomeFeedUiState ->
            render(state)
        })
    }

    private fun render(state: HomeFeedUiState) {
        state.fold({ renderLoading() }, { date -> renderData(date) }) { error -> renderError(error) }
    }

    private fun renderLoading() {
        loading = true
//        signInProgressBar.visibility = View.VISIBLE
    }

    private fun renderData(data: HomeFeedInfo) {
        loading = false

        signInProgressBar.visibility = View.GONE
        adapter.submitList(data.points)
    }

    private fun renderError(error: ErrorBody) {
        loading = false

        when (error.type) {
            ErrorBody.ErrorType.NO_CONTENT -> {
                Toast.makeText(
                    context,
                    "К сожалению, мы ничего не нашли в ваших подписках",
                    Toast.LENGTH_LONG
                ).show()
            }
            ErrorBody.ErrorType.NO_INTERNET -> {
                Toast.makeText(
                    context,
                    "Не можем установить соединение с сервером",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> Toast.makeText(
                context,
                "Упсс..Что-то сломалось. Мы скоро все исправим",
                Toast.LENGTH_LONG
            ).show()
        }
        signInProgressBar.visibility = View.GONE
    }

    private fun initRecycleView() {
        val lm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pointsList.layoutManager = lm
        binding.pointsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = lm.childCount;
                    val totalItemCount = lm.itemCount;
                    val pastVisibleItems = lm.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true
                            vm.loadNextPageIntent()
                        }
                    }
                }
            }
        })
        adapter = HomeFeedAdapter(
            appExecutors = appExecutors,
            pointClickCallback = { point ->
                val direction =
                    HomeFeedFragmentDirections.actionHomeFeedFragmentDestToTripDetailFragment(point.tripId)
                findNavController().navigate(direction)
            },
            userClickCallback = { userId ->
                val action =
                    HomeFeedFragmentDirections.actionHomeFeedFragmentDestToProfileFragment(userId)
                findNavController().navigate(action)
            }
        )
        binding.pointsList.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
