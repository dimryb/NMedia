package ru.netology.nmedia.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import ru.netology.nmedia.databinding.ItemLoadingBinding
import ru.netology.nmedia.presentation.viewholder.PostLoadingViewHolder

class PostLoadingStateAdapter(
    private val onInteractionListener: OnInteractionListener,
) : LoadStateAdapter<PostLoadingViewHolder>() {

    interface OnInteractionListener {
        fun onRetry() {}
    }

    override fun onBindViewHolder(holder: PostLoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PostLoadingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostLoadingViewHolder(
            ItemLoadingBinding.inflate(layoutInflater, parent, false),
            onInteractionListener
        )
    }
}
