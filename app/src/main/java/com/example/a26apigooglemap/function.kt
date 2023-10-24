package com.example.a26apigooglemap

import android.content.Context
import android.widget.ProgressBar
import android.widget.Toast
lateinit var progressBar: ProgressBar
fun toast(context: Context,string: String) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}
