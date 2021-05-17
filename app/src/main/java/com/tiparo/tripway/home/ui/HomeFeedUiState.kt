package com.tiparo.tripway.home.ui

import com.tiparo.tripway.home.api.dto.HomeFeedInfo
import com.tiparo.tripway.utils.ErrorBody
import com.tiparo.tripway.utils.LceUiState
import com.tiparo.tripway.utils.UnaryOperator

class HomeFeedUiState private constructor(
    loading: Boolean = false,
    errorBody: ErrorBody? = null,
    data: HomeFeedInfo? = null
) : LceUiState<HomeFeedInfo>(loading, errorBody, data) {

    companion object {
        fun idle() = HomeFeedUiState()

        fun loading() =
            HomeFeedUiState(loading = true)

        fun data(homeFeedInfo: HomeFeedInfo?) =
            HomeFeedUiState(data = homeFeedInfo)

        fun error(errorBody: ErrorBody?): HomeFeedUiState =
            HomeFeedUiState(errorBody = errorBody)
    }

    class Mutators private constructor() {

        companion object {
            fun homeFeedLoading(): UnaryOperator<HomeFeedUiState> =
                UnaryOperator { loading() }

            fun homeFeedData(homeFeedInfo: HomeFeedInfo?): UnaryOperator<HomeFeedUiState> =
                UnaryOperator { prevState ->
                    val prevList = prevState.data.orNull()?.points ?: listOf()
                    val newList = homeFeedInfo?.points ?: listOf()

                    data(homeFeedInfo?.copy(points = prevList + newList))
                }

            fun homeFeedError(error: Throwable?): UnaryOperator<HomeFeedUiState> =
                UnaryOperator { prevState ->
                    HomeFeedUiState.error(ErrorBody.fromException(error))
                }
        }
    }
}