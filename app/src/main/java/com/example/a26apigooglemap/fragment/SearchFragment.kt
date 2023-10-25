package com.example.a26apigooglemap.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.a26apigooglemap.R
import com.example.a26apigooglemap.Request.PlacesResponse
import com.example.a26apigooglemap.Request.Results
import com.example.a26apigooglemap.databinding.FragmentSearchBinding
import com.example.a26apigooglemap.recyclerView.RecyclerAdapter
import com.example.a26apigooglemap.toast
import kotlin.concurrent.thread


private const val KEY_BUNDLE = "SearchFragment"

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var placesResponse: PlacesResponse? = null
    private lateinit var adapter: RecyclerAdapter
    private var thisContainer: FragmentContainerView? = null
    private var iVShowHideKeyboard: ImageView? = null
    private lateinit var mapFragment: Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        initField()
        setListInRecyclerViewAdapter()
        containerOnlyOnce()

        val map = parentFragmentManager.findFragmentById(R.id.map_fragment_container) as Map
        map.getMapAsync {
            it.setOnMarkerClickListener { marker ->
                binding.recycler
                placesResponse?.let { placesResponse ->
                    val name = marker.title
                    if (name?.isBlank() == true) {
                        toast(requireContext(), "неизвестное место !!")
                    } else {
                        val resultSingle = placesResponse.results.filter { it.name == name }[0]
                        val index = placesResponse.results.indexOf(resultSingle)
                        binding.recycler.smoothScrollToPosition(index)
                    }
                }
                false
            }
        }
        return binding.root
    }

    private fun initField() {
        placesResponse = getPlacesResponseArgument()
        adapter = RecyclerAdapter { model -> getModel(model) }
        PagerSnapHelper().apply { attachToRecyclerView(binding.recycler) }
        parentFragment?.let { mapFragment = it }
        thisContainer = mapFragment.view?.rootView?.findViewById(R.id.search_fragment_container)
        iVShowHideKeyboard = mapFragment.view?.rootView?.findViewById(R.id.iv_show_container)
    }

    private fun getModel(results: Results) {
        val map = parentFragmentManager.findFragmentById(R.id.map_fragment_container) as Map
        val location = "${results.geometry.location.lat},${results.geometry.location.lng}"
        val namePlace = results.name
        map.animationCameraMap(locationFoZoom = location, zoom = 15f, namePlace = namePlace)

    }


    private fun setListInRecyclerViewAdapter() {
        placesResponse?.let { adapter.setList(listResults = it.results) }
        binding.recycler.adapter = adapter
    }

    private fun containerOnlyOnce() {
        //___________________________________________________________________
        if (SaveStateMapObj.visibilityContainer == null) {
            thisContainer?.let {
                thread {
                    Thread.sleep(1500)
                    requireActivity().runOnUiThread {
                        it.isVisible = true
                        SaveStateMapObj.visibilityContainer = true
                        changeIconContainer(it)
                    }
                }
                //__________________________________________________________________
            }
        }


    }


    private fun changeIconContainer(view: View) {
        // change icon
        iVShowHideKeyboard?.let {
            if (view.isVisible) it.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_keyboard_arrow_down_24
                )
            ) else {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.baseline_keyboard_arrow_up_24
                    )
                )
            }
        }

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