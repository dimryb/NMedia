package ru.netology.nmedia.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.nmedia.domain.enumeration.AttachmentType

@Parcelize
data class Attachment(
    val url: String = "",
    val description: String = "",
    val type: AttachmentType = AttachmentType.IMAGE,
) : Parcelable
