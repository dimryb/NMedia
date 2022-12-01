package ru.netology.nmedia.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardTimingBinding
import ru.netology.nmedia.domain.dto.TimingSeparator
import ru.netology.nmedia.util.TimeUtils.TimePeriod

class TimingSeparatorViewHolder(
    private val binding: CardTimingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(timingSeparator: TimingSeparator) {
        binding.text.text = separatorText(timingSeparator.period)
    }

    private fun separatorText(timePeriod: TimePeriod): String =
        when (timePeriod) {
            TimePeriod.THIS_HOUR -> "Сейчас"
            TimePeriod.HOURS_AGO -> "Час назад"
            TimePeriod.TODAY -> "Сегодня"
            TimePeriod.YESTERDAY -> "Вчера"
            TimePeriod.LAST_WEAK -> "На прошлой неделе"
        }
}