package com.example.a26apigooglemap

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.a26apigooglemap.databinding.ActivityMainBinding
import com.example.a26apigooglemap.fragment.MapFragment
import com.example.a26apigooglemap.fragment.SignInFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

interface GetProgressBar {
    fun getProgressBar(): ProgressBar
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), GetProgressBar {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var account: GAccount
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
        //если пользовательне зарешистрирован
        val signInFragment = supportFragmentManager.findFragmentById(R.id.container_fragment)
        if (account.getGoogleAccount() == null) {
            if (signInFragment == null) {
                addFragment(SignInFragment(), true)
            }
        }
        //если зарегестрирован______________________________________________________________
        val mapFragment = supportFragmentManager.findFragmentById(R.id.container_fragment)
        if (account.getGoogleAccount() != null) {
            if (mapFragment == null) {
                binding.progressbar.isVisible = true
                addFragment(MapFragment())
            }
        }
    }

    override fun getProgressBar() = binding.progressbar

}