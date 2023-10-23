package com.example.a26apigooglemap.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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

private const val KEY_BUNDLE = "MapFragment"

@AndroidEntryPoint
class MapFragment : Fragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var binding: MapFragmentBinding
    private lateinit var map: Map
    private var exportData = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MapFragmentBinding.inflate(inflater, container, false)
        Log.d("TAG1", "onCreateView: Fragment")
        initField()
        clickOnSearch()
        clickOnFab()
        showHideContainerFromBundle()
        ivShowHideContainer()
        Log.d("TAG1", "map: $this")

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                MapViewModel.UiState.Empty -> Unit
                MapViewModel.UiState.Loading -> showProgressBar()
                is MapViewModel.UiState.Result -> whenResponse(uiState.responseBody)
                is MapViewModel.UiState.Error -> showError(uiState.error)
            }
        }
        return binding.root
    }

    private fun ivShowHideContainer() {
        binding.ivShowContainer.setOnClickListener {
            val searchFragment =
                childFragmentManager.findFragmentById(R.id.search_fragment_container)
            if (searchFragment is SearchFragment) {
                visibilityContainer(binding.searchFragmentContainer)
            }
        }
    }

    private fun showHideContainerFromBundle() {
        // восстановление состояния контэйнера из бандл
        this.arguments?.let {
            binding.searchFragmentContainer.isVisible = requireArguments().getBoolean(KEY_BUNDLE)
            changeIconIVShowHideContainer(binding.searchFragmentContainer)
        }
    }


    private fun initField() {
        map = (childFragmentManager.findFragmentById(R.id.map_fragment_container) as? Map)!!
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
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


    private fun visibilityContainer(view: View) {
        view.isVisible = !view.isVisible
        // change icon
        changeIconIVShowHideContainer(view)
        //запись положения контейнера в бандл
        this.arguments =
            Bundle().apply { putBoolean(KEY_BUNDLE, binding.searchFragmentContainer.isVisible) }
    }

    private fun changeIconIVShowHideContainer(view: View) {
        if (view.isVisible) binding.ivShowContainer.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_keyboard_arrow_down_24)
        ) else {
            binding.ivShowContainer.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_keyboard_arrow_up_24
                )
            )
        }
    }


    private fun clickOnSearch() {
        binding.ivSearch.setOnClickListener {
            //разрешение на передачу данных только 1 раз
            exportData = true
            val location = binding.etLocation.text.toString()
            val radius = binding.etRadius.text.toString()
            viewModel.getDataNearbyPlaces(location = location, radius = radius)
            map.animationCameraMap(location)
            hideKeyboard()
        }

    }

    private fun hideKeyboard() {
        val view = this.requireActivity().currentFocus
        view?.let {
            val hideMe =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken, 0)
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    private fun exportPlacesResponseToSearchFragment(responseBody: PlacesResponse) {
        if (exportData) {
            val searchFragment = SearchFragment.newInstance(responseBody)
            childFragmentManager.beginTransaction()
                .replace(R.id.search_fragment_container, searchFragment)
                .commit()
            exportData = false
        }
    }


    private fun showError(error: String) {
        toast(requireContext(), error)
    }

    private fun showProgressBar() {
    }
}