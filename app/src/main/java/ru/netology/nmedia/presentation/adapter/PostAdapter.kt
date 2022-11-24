package ru.netology.nmedia.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardTestBinding
import ru.netology.nmedia.databinding.CardTimingBinding
import ru.netology.nmedia.domain.dto.Ad
import ru.netology.nmedia.domain.dto.FeedItem
import ru.netology.nmedia.domain.dto.Post
import ru.netology.nmedia.domain.dto.TimingSeparator
import ru.netology.nmedia.presentation.view.load
import ru.netology.nmedia.presentation.viewholder.AdViewHolder
import ru.netology.nmedia.presentation.viewholder.OnInteractionListener
import ru.netology.nmedia.presentation.viewholder.PostViewHolder
import ru.netology.nmedia.presentation.viewholder.TimingSeparatorViewHolder

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TimingSeparator -> R.layout.card_timing
            //null -> error("unknown item type")
            null -> {
                println("getItemViewType: item type null")
                R.layout.card_test
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.card_timing -> {
                val binding =
                    CardTimingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TimingSeparatorViewHolder(binding)
            }
            R.layout.card_test -> {
                val binding =
                    CardTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                println("onCreateViewHolder: TestViewHolder")
                TestViewHolder(binding)
            }
            else -> error("unknown view type: $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is TimingSeparator -> (holder as? TimingSeparatorViewHolder)?.bind(item)
            //null -> error("unknown view type")
            null -> {
                println("onCreateViewHolder: null")
                (holder as? TestViewHolder)?.bind()
            }
        }
    }
}

class TestViewHolder(
    private val binding: CardTestBinding,
) : RecyclerView.ViewHolder(binding.root){
    fun bind(){
        binding.root
    }
}