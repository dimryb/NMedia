package ru.netology.nmedia.presentation.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.nmedia.BuildConfig

fun ImageView.load(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    Glide.with(this)
        .load(url)
        .timeout(10_000)
        .transform(*transforms)
        .into(this)

fun ImageView.loadCircleCrop(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    load(url, CircleCrop(), *transforms)

fun String.fullAvatarsUrl() = "${BuildConfig.BASE_URL}/avatars/$this"
fun String.fullImagesUrl() = "${BuildConfig.BASE_URL}/images/$this"

fun ImageView.loadAuthorAvatar(fileName: String) = loadCircleCrop(fileName.fullAvatarsUrl())

fun ImageView.loadImageMedia(fileName: String) {
    val imageWidth =
        982 // TODO: разобраться как правильно определить необходимые размеры изображения медиа
    Glide.with(this)
        .load(fileName.fullImagesUrl())
        .override(imageWidth)
        .timeout(10_000)
        .into(this)
}