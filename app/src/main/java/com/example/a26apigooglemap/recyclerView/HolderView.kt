package com.example.a26apigooglemap.recyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a26apigooglemap.Request.Results
import com.example.a26apigooglemap.databinding.ItemPhotoBinding

private const val URL_IN =
    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photo_reference="

private const val URL_END = "&key=AIzaSyDlely2vEZ0ymNqr4SLZIn54Ak5xzvoNuk"

class HolderPlace(private val view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemPhotoBinding.bind(view)
    fun initView(results: Results) {
        binding.tv.text = results.name

        Glide.with(view.context).load(
            URL_IN+"${results.photos[0].photo_reference}"+ URL_END
        ).into(binding.itemPhoto)
    }
}