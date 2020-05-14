/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tiparo.tripway.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.TripOwnItemBinding
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.views.common.DataBoundListAdapter

/**
 * A RecyclerView adapter for [Repo] class.
 */
class TripsListAdapter(
    appExecutors: AppExecutors,
    private val tripClickCallback: ((Trip) -> Unit)?
) : DataBoundListAdapter<Trip, TripOwnItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(
            oldItem: Trip,
            newItem: Trip
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Trip,
            newItem: Trip
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): TripOwnItemBinding {
        val binding = DataBindingUtil.inflate<TripOwnItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.trip_own_item,
            parent,
            false
        )
        binding.root.setOnClickListener {
            binding.trip?.let {
                tripClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: TripOwnItemBinding, item: Trip) {
        binding.trip = item
    }
}
