package ru.netology.nmedia.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardTimingBinding
import ru.netology.nmedia.domain.dto.TimingSeparator

class TimingSeparatorViewHolder(
    private val binding: CardTimingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(timingSeparator: TimingSeparator) {
        binding.text.text = timingSeparator.text
    }
}