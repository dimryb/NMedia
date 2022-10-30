package ru.netology.nmedia.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    val url: String = "",
    val description: String = "",
    val type: AttachmentType = AttachmentType.IMAGE,
) : Parcelable

enum class AttachmentType {
    IMAGE
}