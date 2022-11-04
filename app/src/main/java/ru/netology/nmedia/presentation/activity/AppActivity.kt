package ru.netology.nmedia.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.presentation.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.presentation.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel by viewModels<AuthViewModel>()

    private var currentMenuProvider: MenuProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        checkGoogleApiAvailability()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.data.observe(this) {
            currentMenuProvider?.also(::removeMenuProvider)
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                    val authorized = viewModel.authorized
                    menu.setGroupVisible(R.id.authorized, authorized)
                    menu.setGroupVisible(R.id.unauthorized, !authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.signIn -> {
                            viewModel.signIn()
                            true
                        }
                        R.id.signUp -> {
                            TODO("Sign Up")
                            true
                        }
                        R.id.logout -> {
                            viewModel.signOutAsk()
                            true
                        }
                        else -> false
                    }


            }.apply {
                currentMenuProvider = this
            })
        }

        viewModel.token.observe(this) { token ->
            println("Token ${token.id} ${token.token}")
            AppAuth.getInstance().setAuth(token.id, token.token ?: "")
            supportFragmentManager.popBackStack()
        }

        viewModel.signIn.observe(this) {
            println("Sign In")
            supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, SignInFragment())
                .addToBackStack("SignIn")
                .commit()
        }
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, "Google Api Unavailable", Toast.LENGTH_LONG)
                .show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
        }
    }
}