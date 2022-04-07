package com.tiparo.tripway.home.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.PointItemBinding
import com.tiparo.tripway.home.api.dto.Point
import com.tiparo.tripway.views.common.DataBoundListAdapter

class HomeFeedAdapter(
    appExecutors: AppExecutors,
    private val pointClickCallback: ((Point) -> Unit),
    private val userClickCallback: ((String) -> Unit)
) : DataBoundListAdapter<Point, PointItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Point>() {
        override fun areItemsTheSame(
            oldItem: Point,
            newItem: Point
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Point,
            newItem: Point
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): PointItemBinding {
        val binding = DataBindingUtil.inflate<PointItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.point_item,
            parent,
            false
        )
        binding.root.setOnClickListener {
            pointClickCallback.invoke(binding.point!!)
        }
        binding.username.setOnClickListener {
            userClickCallback.invoke(binding.point!!.userId)
        }
        return binding
    }

    override fun bind(context: Context, binding: PointItemBinding, item: Point) {
        binding.point = item
        Glide.with(binding.root.context)
            .load(item.photo)
            .placeholder(R.drawable.card_own_placeholder)
            .into(binding.pointImage)
    }
}