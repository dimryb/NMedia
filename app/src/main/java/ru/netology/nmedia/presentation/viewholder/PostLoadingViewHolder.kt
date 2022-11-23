package ru.netology.nmedia.presentation.viewholder

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.ItemLoadingBinding
import ru.netology.nmedia.presentation.adapter.PostLoadingStateAdapter

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