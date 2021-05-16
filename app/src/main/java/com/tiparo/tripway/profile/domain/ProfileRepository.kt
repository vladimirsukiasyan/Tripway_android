package com.tiparo.tripway.profile.domain

import com.tiparo.tripway.profile.api.dto.ProfileInfo
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.utils.Either
import com.tiparo.tripway.utils.RxUnit
import com.tiparo.tripway.utils.Transformers
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val tripsService: TripsService
) {
    fun getProfile(userId: String?): Observable<Either<Throwable, ProfileInfo>> =
        tripsService.getProfile(userId)
            .toObservable()
            .doOnError { er: Throwable -> Timber.e(er.toString()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(Transformers.neverThrowO())

    fun subscribeTo(userId: String): Observable<Either<Throwable, *>> {
        return tripsService.subscribeToUser(userId)
            .defaultIfEmpty(RxUnit)
            .doOnError { er: Throwable -> Timber.e(er.toString()) }
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(Transformers.neverThrowO())
    }

    fun unsubscribeFrom(userId: String): Observable<Either<Throwable, *>> {
        return tripsService.unsubscribeFromUser(userId)
            .defaultIfEmpty(RxUnit)
            .doOnError { er: Throwable -> Timber.e(er.toString()) }
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(Transformers.neverThrowO())
    }
}