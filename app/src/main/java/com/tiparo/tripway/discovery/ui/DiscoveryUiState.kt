package com.tiparo.tripway.discovery.ui

import com.tiparo.tripway.discovery.api.dto.DiscoveryInfo
import com.tiparo.tripway.utils.ErrorBody
import com.tiparo.tripway.utils.LceUiState
import com.tiparo.tripway.utils.UnaryOperator

class DiscoveryUiState private constructor(
    loading: Boolean = false,
    errorBody: ErrorBody? = null,
    data: DiscoveryInfo? = null
): LceUiState<DiscoveryInfo>(loading, errorBody, data) {

    companion object {
        fun idle() = DiscoveryUiState()

        fun loading() =
            DiscoveryUiState(loading = true)

        fun data(discoveryInfo: DiscoveryInfo?) =
            DiscoveryUiState(data = discoveryInfo)

        fun error(errorBody: ErrorBody?): DiscoveryUiState =
            DiscoveryUiState(errorBody = errorBody)
    }

    class Mutators private constructor() {

        companion object {
            fun discoveryLoading(): UnaryOperator<DiscoveryUiState> =
                UnaryOperator { loading() }

            fun discoveryData(discoveryInfo: DiscoveryInfo?): UnaryOperator<DiscoveryUiState> =
                UnaryOperator {prevState->
                    data(discoveryInfo)
                }

            fun discoveryError(error: Throwable?): UnaryOperator<DiscoveryUiState> =
                UnaryOperator {prevState->
                    DiscoveryUiState.error(ErrorBody.fromException(error))
                }
        }
    }
}