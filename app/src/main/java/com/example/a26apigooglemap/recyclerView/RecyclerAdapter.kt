package com.example.a26apigooglemap.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a26apigooglemap.R
import com.example.a26apigooglemap.Request.Results

class RecyclerAdapter(
    private val clickOnItem: (results: Results) -> Unit = {}
) : RecyclerView.Adapter<HolderPlace>() {
    private var listItem: List<Results> = emptyList()
    fun setList(listResults: List<Results>) {
        listItem = listResults
        notifyDataSetChanged()
    }

    override fun getItemCount() = listItem.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPlace {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return HolderPlace(view)
    }

    override fun onBindViewHolder(holder: HolderPlace, position: Int) {
        holder.initView(listItem[position],clickOnItem)
    }
}

