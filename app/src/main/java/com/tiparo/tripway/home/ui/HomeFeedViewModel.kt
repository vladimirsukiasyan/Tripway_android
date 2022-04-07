package com.tiparo.tripway.home.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiparo.tripway.home.api.dto.HomeFeedInfo
import com.tiparo.tripway.home.ui.HomeFeedUiState.Mutators
import com.tiparo.tripway.posting.domain.PostRepository
import com.tiparo.tripway.utils.RxUnit
import com.tiparo.tripway.utils.Transformers.startWithInMain
import com.tiparo.tripway.utils.UnaryOperator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class HomeFeedViewModel @Inject constructor(private val postRepository: PostRepository) :
    ViewModel() {

    val query = MutableLiveData<String>()
    val uiStateLiveData = MutableLiveData<HomeFeedUiState>()
    private val compositeDisposable = CompositeDisposable()

    private val loadFirstPageIntent = PublishSubject.create<RxUnit>()
    private val retryFirstPageIntent = PublishSubject.create<RxUnit>()
    private val loadNextPageIntent = PublishSubject.create<RxUnit>()
    private val innerState: BehaviorSubject<HomeFeedUiState> = BehaviorSubject.create()


    init {
        val disposable = Observable.merge(firstPageChanges(), nextPageChanges())
            .scan(HomeFeedUiState.idle(), { prevState, mutator ->
                mutator.apply(prevState)
            })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(innerState::onNext)
            .subscribe { value: HomeFeedUiState -> uiStateLiveData.postValue(value) }

        compositeDisposable.add(disposable)
    }

    fun loadFirstPageIntent() {
        loadFirstPageIntent.onNext(RxUnit.INSTANCE)
    }

    fun loadNextPageIntent() {
        loadNextPageIntent.onNext(RxUnit.INSTANCE)
    }

    fun retryFirstPageIntent() {
        retryFirstPageIntent.onNext(RxUnit.INSTANCE)
    }

    private fun firstPageChanges(): Observable<UnaryOperator<HomeFeedUiState>> {
        return Observable.merge(loadFirstPageIntent, retryFirstPageIntent)
            .flatMap {
                postRepository.homeFeedFirstPageResult()
                    .map { result ->
                        if (result.isRight) {
                            Mutators.homeFeedData(result.right)
                        } else {
                            Mutators.homeFeedError(result.left)
                        }
                    }
                    .compose(startWithInMain(Mutators.homeFeedLoading()))
            }
    }

    private fun nextPageChanges(): Observable<UnaryOperator<HomeFeedUiState>> {
        return loadNextPageIntent
            .withLatestFrom(
                innerState,
                BiFunction<Any, HomeFeedUiState, HomeFeedUiState> { _, s -> s })
            .map { it.data.orNull() }
            .filter { d: HomeFeedInfo? -> d != null && d.hasMore ?: false }
            .flatMap { pointsNextPageResult() }

    }

    private fun pointsNextPageResult(): Observable<UnaryOperator<HomeFeedUiState>> {
        return postRepository.homeFeedNextPageResult()
            .map { result ->
                if (result.isRight) Mutators.homeFeedData(result.right)
                else Mutators.homeFeedError(result.left)
            }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}