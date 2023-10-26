package com.example.a26apigooglemap

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val WEB_CLIENT_API_KEY =
    "192508560434-1ic2hjvvqf8air30u7bimjnkd9547qir.apps.googleusercontent.com"

class GAccount @Inject constructor(@ApplicationContext private val context: Context) {
    fun getSingInIntent(): Intent {
        val googleOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_API_KEY)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, googleOptions)
        return googleSignInClient.signInIntent
    }

    fun getGoogleAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun signInAccount(intent: Intent?, success: (success: Boolean) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            val result = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(result.idToken, null)
            val auth = FirebaseAuth.getInstance()
            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    success(it.isSuccessful)
                    if (it.isSuccessful) toast(context,"Выполняется вход")
                    if (!it.isSuccessful) toast(context,"Firebase Error")
                }
        } catch (e: ApiException) {
            toast(context,e.message.toString())
        }
    }

}