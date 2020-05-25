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

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.R
import com.tiparo.tripway.views.ui.PointFragment
import kotlinx.android.synthetic.main.point_photo_item.view.*
import timber.log.Timber

class PointPhotosAdapter(
    private val photoClickCallback: ((Uri) -> Unit)?
) : RecyclerView.Adapter<PointPhotosAdapter.ViewHolder>() {
    var photosUriList: List<Uri> = listOf()

    override fun getItemCount() = photosUriList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photosUriList[position], photoClickCallback)
    }

    class ViewHolder(val view: View, val imageViewSize: Int) : RecyclerView.ViewHolder(view) {
        fun bind(uri: Uri, photoClickCallback: ((Uri) -> Unit)?) {
            val layoutParams = view.pointImage.layoutParams
            layoutParams.height = imageViewSize

            view.pointImage.layoutParams = layoutParams

            view.pointImage.setOnClickListener {
                photoClickCallback?.invoke(uri)
            }

            Glide.with(view.context.applicationContext)
                .load(uri)
                .placeholder(R.drawable.trip_card_own_placeholder)
                .into(view.pointImage)
        }

        companion object {
            var imageViewSize: Int = 0

            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                if (imageViewSize == 0) {
                    imageViewSize = (parent.rootView.width - parent.context.resources.getDimensionPixelSize(R.dimen.images_grid_spacing) * (PointFragment.SPAN_COUNT - 1)) / PointFragment.SPAN_COUNT
                }

                return ViewHolder(
                    view = layoutInflater.inflate(
                        R.layout.point_photo_item,
                        parent,
                        false
                    ), imageViewSize = imageViewSize
                )
            }
        }

    }
}
