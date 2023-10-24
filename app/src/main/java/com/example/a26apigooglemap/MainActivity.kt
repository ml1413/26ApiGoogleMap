package com.example.a26apigooglemap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.a26apigooglemap.databinding.ActivityMainBinding
import com.example.a26apigooglemap.fragment.MapFragment
import com.example.a26apigooglemap.fragment.SignInFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var account: GAccount
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        progressBar = binding.progressbar
        //запуск нужного фрагмента (если аккаунт null франмент с аутентификацией если не null фрагмент с картой)
        launchFragment()
    }


    private fun addFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        supportFragmentManager.beginTransaction().apply {
            if (addToBackStack) addToBackStack("")
            add(R.id.container_fragment, fragment)
            commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 123) account.signInAccount(intent) { success ->
            if (success) launchFragment()
        }
    }

    private fun launchFragment() {
        if (account.getGoogleAccount() == null) addFragment(SignInFragment(), true)
        if (account.getGoogleAccount() != null
            && supportFragmentManager.findFragmentById(R.id.container_fragment) !is MapFragment
        ) {
            progressBar.isVisible = true
            addFragment(MapFragment())
        }

    }


}