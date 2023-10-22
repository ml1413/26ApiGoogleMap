package com.example.a26apigooglemap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a26apigooglemap.R
import com.example.a26apigooglemap.Request.ComplexRouteResponse
import com.example.a26apigooglemap.Request.DirectionsResponse
import com.example.a26apigooglemap.Request.PlacesResponse
import com.example.a26apigooglemap.databinding.MapFragmentBinding
import com.example.a26apigooglemap.toast
import com.example.a26apigooglemap.viewModel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var binding: MapFragmentBinding
    private lateinit var map: Map

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MapFragmentBinding.inflate(inflater, container, false)
        initField()
        clickOnSearch()
        clickOnFab()
        viewModel.uiState.observe(viewLifecycleOwner)
        { uiState ->
            when (uiState) {
                MapViewModel.UiState.Empty -> Unit
                MapViewModel.UiState.Loading -> showProgressBar()
                is MapViewModel.UiState.Result -> whenResponse(uiState.responseBody)
                is MapViewModel.UiState.Error -> showError(uiState.error)
            }
        }
        return binding.root
    }

    private fun initField() {
        map = (childFragmentManager.findFragmentById(R.id.map_fragment_container) as? Map)!!
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
    }

    private fun clickOnFab() {
        binding.fab2.setOnClickListener {

            val waypointCoordinates = CoordinateLatLng1.placeCoordinate.drop(0).take(10)
            val waypointCoordinateString = waypointCoordinates.joinToString(separator = "|")
            viewModel.getDataComplexRoute(
                originId = CoordinateLatLng1.placeCoordinate[0],
                destinationId = CoordinateLatLng1.placeCoordinate.last(),
                waypoints = waypointCoordinateString
            )
        }
    }

    private fun clickOnSearch() {
        binding.ivSearch.setOnClickListener {
            val location = binding.etLocation.text.toString()
            val radius = binding.etRadius.text.toString()
            viewModel.getDataNearbyPlaces(location = location, radius = radius)
            map.animationCameraMap(location)
        }
    }


    private fun whenResponse(responseBody: Any?) {
        when (responseBody) {
            is DirectionsResponse -> map.showMapBuildLine(responseBody)
            is PlacesResponse -> {
                map.showMapNearbyPlaces(responseBody)
                exportPlacesResponseToSearchFragment(responseBody)
            }

            is ComplexRouteResponse -> map.showMapComplexRoute(responseBody)
        }
    }

    private fun exportPlacesResponseToSearchFragment(responseBody: PlacesResponse) {
        val searchFragment = SearchFragment.newInstance(responseBody)
        childFragmentManager.beginTransaction()
            .replace(R.id.search_fragment, searchFragment)
            .commit()
    }


    private fun showError(error: String) {
        toast(requireContext(), error)
    }

    private fun showProgressBar() {
    }
}