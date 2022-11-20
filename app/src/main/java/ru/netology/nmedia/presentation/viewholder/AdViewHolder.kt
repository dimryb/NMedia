package ru.netology.nmedia.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.domain.dto.Ad
import ru.netology.nmedia.presentation.view.load

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root){
    fun bind(ad: Ad){
        binding.image.load("${ BuildConfig.BASE_URL}/media/${ad.image}")
    }
}