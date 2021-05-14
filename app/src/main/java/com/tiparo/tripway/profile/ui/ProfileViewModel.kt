package com.tiparo.tripway.profile.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiparo.tripway.discovery.api.dto.DiscoveryInfo
import com.tiparo.tripway.discovery.ui.DiscoveryUiState.Mutators
import com.tiparo.tripway.posting.ui.OwnTripsListUiState
import com.tiparo.tripway.profile.domain.ProfileRepository
import com.tiparo.tripway.repository.TripsRepository
import com.tiparo.tripway.utils.RxUnit
import com.tiparo.tripway.utils.Transformers
import com.tiparo.tripway.utils.Transformers.startWithInMain
import com.tiparo.tripway.utils.UnaryOperator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {
    val uiStateLiveData = MutableLiveData<ProfileUiState>()
    private val compositeDisposable = CompositeDisposable()

    private val initialIntent = PublishSubject.create<RxUnit>()
    private val retryIntent = PublishSubject.create<RxUnit>()

    init {
        val disposable = getProfile(profileRepository)
            .scan(ProfileUiState.idle(), { prevState, mutator ->
                mutator.apply(prevState)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { value: ProfileUiState -> uiStateLiveData.postValue(value) }

        compositeDisposable.add(disposable)
    }

    fun loadProfile() {
        initialIntent.onNext(RxUnit.INSTANCE)
    }

    fun retryLoadProfile() {
        retryIntent.onNext(RxUnit.INSTANCE)
    }

    private fun getProfile(repository: ProfileRepository): Observable<UnaryOperator<ProfileUiState>> {
        return Observable.merge(initialIntent, retryIntent)
            .flatMap {
                repository.getProfile()
                    .map { result ->
                        if (result.isRight) {
                            ProfileUiState.Mutators.discoveryData(result.right)
                        } else {
                            ProfileUiState.Mutators.discoveryError(result.left)
                        }
                    }
                    .compose(Transformers.startWithInMain(ProfileUiState.Mutators.discoveryLoading()))
            }
    }
}