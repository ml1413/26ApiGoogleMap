package com.example.a26apigooglemap.fragment

import android.app.ActionBar.LayoutParams
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.a26apigooglemap.Request.PlacesResponse
import com.example.a26apigooglemap.databinding.FragmentSearchBinding
import com.example.a26apigooglemap.recyclerView.RecyclerAdapter
import javax.inject.Inject

private const val KEY_BUNDLE = "key"


class SearchFragment @Inject constructor() : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var placesResponse: PlacesResponse? = null
    private lateinit var adapter: RecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        initField()
        placesResponse?.let { adapter.setList(listResults = it.results) }
        binding.recycler.adapter = adapter

        val params = LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
        )
        params.weight = 1f
        binding.recycler.layoutParams = params

        return binding.root
    }

    private fun initField() {
        placesResponse = getPlacesResponseArgument()
        adapter = RecyclerAdapter()
    }


    companion object {
        fun newInstance(responseBody: PlacesResponse): SearchFragment {
            val args = Bundle()
            args.putSerializable(KEY_BUNDLE, responseBody)
            val fragment = SearchFragment()
            fragment.arguments = args
            return fragment
        }
    }


    private fun getPlacesResponseArgument(): PlacesResponse? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(
                KEY_BUNDLE,
                PlacesResponse::class.java
            )
        } else {
            requireArguments().getSerializable(KEY_BUNDLE) as PlacesResponse
        }
    }

}