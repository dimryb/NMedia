package ru.netology.nmedia.presentation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.activity.NewPostFragment

class EditPostActivityContract : ActivityResultContract<String, String?>() {

    override fun createIntent(context: Context, input: String): Intent =
        Intent(context, NewPostFragment::class.java)
            .putExtra(Intent.EXTRA_TEXT, input)

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        intent?.getStringExtra(Intent.EXTRA_TEXT)

}