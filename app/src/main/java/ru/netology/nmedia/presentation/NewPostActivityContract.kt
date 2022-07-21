package ru.netology.nmedia.presentation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.activity.NewPostFragment

class NewPostActivityContract : ActivityResultContract<Unit, String?>() {

    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(context, NewPostFragment::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        intent?.getStringExtra(Intent.EXTRA_TEXT)

}