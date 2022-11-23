package ru.netology.nmedia.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.ItemLoadingBinding

class PostLoadingStateAdapter (
    private val onInteractionListener: OnInteractionListener,
) : LoadStateAdapter<PostLoadingViewHolder>() {

    interface OnInteractionListener {
        fun onRetry() {}
    }

    override fun onBindViewHolder(holder: PostLoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PostLoadingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostLoadingViewHolder(
            ItemLoadingBinding.inflate(layoutInflater, parent, false),
            onInteractionListener
        )
    }
}

class PostLoadingViewHolder(
    private val itemLoadingBinding: ItemLoadingBinding,
    private val onInteractionListener: PostLoadingStateAdapter.OnInteractionListener,
) : RecyclerView.ViewHolder(itemLoadingBinding.root) {

    fun bind(loadState: LoadState) {
        itemLoadingBinding.apply {
            progress.isVisible = loadState is LoadState.Loading
            retry.isVisible = loadState is LoadState.Error

            retry.setOnClickListener {
                onInteractionListener.onRetry()
            }
        }
    }
}