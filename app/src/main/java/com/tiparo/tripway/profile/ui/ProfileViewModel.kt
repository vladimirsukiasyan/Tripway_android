package com.tiparo.tripway.profile.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiparo.tripway.profile.domain.ProfileRepository
import com.tiparo.tripway.utils.RxUnit
import com.tiparo.tripway.utils.Transformers
import com.tiparo.tripway.utils.UnaryOperator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ProfileViewModel @Inject constructor(profileRepository: ProfileRepository) : ViewModel() {
    val uiStateLiveData = MutableLiveData<ProfileUiState>()
    val subscribeStatusLivaData = MutableLiveData<Boolean>()
    val unsubscribeStatusLivaData = MutableLiveData<Boolean>()
    private val compositeDisposable = CompositeDisposable()

    private val initialIntent = PublishSubject.create<Any>()
    private val retryIntent = PublishSubject.create<Any>()
    private val subscribeIntent = PublishSubject.create<String>()
    private val unsubscribeIntent = PublishSubject.create<String>()

    init {
        val disposableProfile = getProfile(profileRepository)
            .scan(ProfileUiState.idle(), { prevState, mutator ->
                mutator.apply(prevState)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { value: ProfileUiState -> uiStateLiveData.postValue(value) }

        compositeDisposable.add(disposableProfile)

        val disposableSubscribe = subscribeIntent
            .flatMap { userId ->
                profileRepository.subscribeTo(userId)
                    .map { result -> result.isRight }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isSuccess: Boolean -> subscribeStatusLivaData.postValue(isSuccess) }

        val disposableUnsubscribe = unsubscribeIntent
            .flatMap { userId ->
                profileRepository.unsubscribeFrom(userId)
                    .map { result -> result.isRight }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isSuccess: Boolean -> unsubscribeStatusLivaData.postValue(isSuccess) }

        compositeDisposable.addAll(disposableProfile, disposableSubscribe, disposableUnsubscribe)
    }

    fun loadProfile(userId: String?) {
        initialIntent.onNext(userId ?: RxUnit.INSTANCE)
    }

    fun retryLoadProfile(userId: String?) {
        retryIntent.onNext(userId ?: RxUnit.INSTANCE)
    }

    private fun getProfile(repository: ProfileRepository): Observable<UnaryOperator<ProfileUiState>> {
        return Observable.merge(initialIntent, retryIntent)
            .flatMap {
                val userId = if (it == RxUnit.INSTANCE) null else it as String
                repository.getProfile(userId)
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

    fun subscribeToUser(uid: String) {
        subscribeIntent.onNext(uid)
    }

    fun unsubscribeToUser(uid: String) {
        unsubscribeIntent.onNext(uid)
    }
}