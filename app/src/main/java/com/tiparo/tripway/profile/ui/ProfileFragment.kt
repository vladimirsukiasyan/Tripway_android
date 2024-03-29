package com.tiparo.tripway.profile.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentProfilePageBinding
import com.tiparo.tripway.profile.api.dto.ProfileInfo
import com.tiparo.tripway.profile.ui.adapter.ProfileAdapter
import com.tiparo.tripway.utils.ErrorBody
import kotlinx.android.synthetic.main.fragment_profile_page.*
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private lateinit var binding: FragmentProfilePageBinding
    private lateinit var adapter: ProfileAdapter
    private val vm: ProfileViewModel by viewModels {
        viewModelFactory
    }

    private val args: ProfileFragmentArgs by navArgs()

    private var userId: String? = null

    private lateinit var mProfile: ProfileInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        userId = args.userId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_page, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView()

        vm.loadProfile(userId)

        vm.uiStateLiveData.observe(viewLifecycleOwner, Observer {
            render(it)
        })

        vm.subscribeStatusLivaData.observe(viewLifecycleOwner, Observer {isSuccess->
            if(isSuccess){
                (profile_btn.background as GradientDrawable).apply {
                    mutate()
                    setColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                }
                profile_btn.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorText
                    )
                )
                profile_btn.text = resources.getString(R.string.unsubscribe_btn)
                profile_btn.setOnClickListener {
                    vm.unsubscribeToUser(mProfile.id)
                }
            }
            else {
                Toast.makeText(context, "Не удалось подписаться на пользователя, повторите позже", Toast.LENGTH_LONG).show()
            }
        })

        vm.unsubscribeStatusLivaData.observe(viewLifecycleOwner, Observer {isSuccess->
            if(isSuccess){
                (profile_btn.background as GradientDrawable).apply {
                    mutate()
                    setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
                profile_btn.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextOnButton
                    )
                )
                profile_btn.text = resources.getString(R.string.subscribe_btn)
                profile_btn.setOnClickListener {
                    vm.subscribeToUser(mProfile.id)
                }
            }
            else {
                Toast.makeText(context, "Не удалось подписаться на пользователя, повторите позже", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.signOut -> {
                vm.signOut()
                val action = ProfileFragmentDirections.actionProfileFragmentDestToLoginFragmentDest()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //todo в теории это все можно обернуть и предоставить фрагментам интерфейс (аля обертка для MVI)
    private fun render(state: ProfileUiState) {
        state.fold(
            { renderLoading() },
            { data -> renderData(data) }) { error -> renderError(error) }
    }

    private fun renderLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun renderData(profile: ProfileInfo) {
        progress_bar.visibility = View.GONE

        nickname.text = profile.nickname
        trips_count.text = profile.trips.size.toString()
        subscribersCount.text = profile.subscribersCount.toString()
        subscriptionsCount.text = profile.subscriptionsCount.toString()
        //todo оформить в виде кастомного TextView
        when {
            profile.isOwnProfile -> {
//                (profile_btn.background as GradientDrawable).apply {
//                    mutate()
//                    setColor(ContextCompat.getColor(requireContext(), android.R.color.white))
//                }
//                profile_btn.setTextColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.colorText
//                    )
//                )
//                profile_btn.text = resources.getString(R.string.edit_profile_btn)
                profile_btn.visibility = View.GONE
            }
            profile.isSubscription -> {
                (profile_btn.background as GradientDrawable).apply {
                    mutate()
                    setColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                }
                profile_btn.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorText
                    )
                )
                profile_btn.text = resources.getString(R.string.unsubscribe_btn)
                profile_btn.setOnClickListener {
                    vm.unsubscribeToUser(profile.id)
                }
            }
            else -> {
                (profile_btn.background as GradientDrawable).apply {
                    mutate()
                    setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
                profile_btn.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextOnButton
                    )
                )
                profile_btn.text = resources.getString(R.string.subscribe_btn)
                profile_btn.setOnClickListener {
                    vm.subscribeToUser(profile.id)
                }
            }
        }
        //todo avatar.setImageURI(data.avatar)
        adapter.submitList(profile.trips)
        mProfile = profile
    }

    private fun renderError(error: ErrorBody) {
        progress_bar.visibility = View.GONE
        when (error.type) {
            ErrorBody.ErrorType.NO_INTERNET -> {
                Toast.makeText(
                    context,
                    "Не можем установить соединение с сервером",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> Toast.makeText(
                context,
                "Неизвестная ошибка, но мы скоро все исправим",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initRecycleView() {
        val lm = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        binding.trips.layoutManager = lm
        adapter = ProfileAdapter(
            appExecutors = appExecutors,
            tripClickCallback = { trip ->
                val direction = ProfileFragmentDirections.actionProfileFragmentDestToTripDetailFragment(trip.id)
                findNavController().navigate(direction)
            }
        )
        binding.trips.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    companion object {
        const val ARG_USER_ID = "user_id"
    }
}