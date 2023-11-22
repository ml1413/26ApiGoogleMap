package com.example.a26apigooglemap.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a26apigooglemap.GetProgressBar
import com.example.a26apigooglemap.R
import com.example.a26apigooglemap.Request.ComplexRouteResponse
import com.example.a26apigooglemap.Request.DirectionsResponse
import com.example.a26apigooglemap.Request.PlacesResponse
import com.example.a26apigooglemap.databinding.MapFragmentBinding
import com.example.a26apigooglemap.toast
import com.example.a26apigooglemap.viewModel.MapRoadLineViewMode
import com.example.a26apigooglemap.viewModel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread

object SaveStateMapObj {
    var visibilityContainer: Boolean? = null
    var alphaFab = 0f
    var radius = "2000"
    var currentLocation = ""
    var locationOnMapMarker = ""
}

@AndroidEntryPoint
class MapFragment : Fragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var viewModelRoadLine: MapRoadLineViewMode
    private lateinit var binding: MapFragmentBinding
    private lateinit var map: Map
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var exportData = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MapFragmentBinding.inflate(inflater, container, false)
        progressBarVisibilityOff()
        initField()
        clickOnSearch()
        clickOnFab()
        clickOnRadius()
        clickIvShowHideContainer()
        showHideContainerFromBundle()
        showFabFromVisibilityObj()
        setValueInTVRadius(SaveStateMapObj.radius)
        clickSelfLocation()
        isShowMyLocationOnMap()

        //___________________________________________________________________________
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                MapViewModel.UiState.Empty -> Unit
                MapViewModel.UiState.Loading -> showProgressBar()
                is MapViewModel.UiState.Result -> whenResponse(uiState.responseBody)
                is MapViewModel.UiState.Error -> showError(uiState.error)
            }
        }
        //___________________________________________________________________________
        viewModelRoadLine._uiState.observe(viewLifecycleOwner) { uiStateRL ->
            when (uiStateRL) {
                MapRoadLineViewMode.UiStateRL.Empty -> Unit
                MapRoadLineViewMode.UiStateRL.Loading -> showProgressBar()
                is MapRoadLineViewMode.UiStateRL.Result -> whenResponse(uiStateRL.directionsResponse)
                is MapRoadLineViewMode.UiStateRL.Error -> showError(uiStateRL.error)
            }
        }
        return binding.root
    }

    private fun isShowMyLocationOnMap() {
        if (SaveStateMapObj.currentLocation.isNotBlank()) {
            map.animationCameraMap(
                SaveStateMapObj.currentLocation,
                namePlace = resources.getString(R.string.You)
            )
        }
    }

    private fun initField() {
        childFragmentManager.findFragmentById(R.id.map_fragment_container).let { map = it as Map }
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        viewModelRoadLine = ViewModelProvider(this)[MapRoadLineViewMode::class.java]
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
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


    private fun clickSelfLocation() {
        binding.ivLocation.setOnClickListener {
            location()
        }
    }


    private fun location() {
        val tack = fusedLocation.lastLocation

        val selfPermission = ActivityCompat
            .checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        val selfPermission2 = ActivityCompat
            .checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)

        val permissionGranted = PackageManager.PERMISSION_GRANTED

        if (selfPermission != permissionGranted && selfPermission2 != permissionGranted) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 123
            )
            return
        }
        tack.addOnSuccessListener {

            if (it == null) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                toast(requireContext(), "Location must be on")
            } else {
                SaveStateMapObj.currentLocation = "${it.latitude},${it.longitude}"
                map.animationCameraMap(
                    SaveStateMapObj.currentLocation,
                    namePlace = resources.getString(R.string.You)
                )
                binding.etLocation.setText(SaveStateMapObj.currentLocation)
                showFab()
            }
        }
    }

    private fun clickOnRadius() {
        binding.ivRadius.setOnClickListener {
            shopPopUpMenu(it)
        }
    }

    private fun shopPopUpMenu(v: View) {
        PopupMenu(requireContext(), v).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.i500 -> SaveStateMapObj.radius = "500"
                    R.id.i1000 -> SaveStateMapObj.radius = "1000"
                    R.id.i1500 -> SaveStateMapObj.radius = "1500"
                    R.id.i2000 -> SaveStateMapObj.radius = "2000"
                    R.id.i2500 -> SaveStateMapObj.radius = "2500"
                    R.id.i3000 -> SaveStateMapObj.radius = "3000"
                }
                setValueInTVRadius(SaveStateMapObj.radius)
                true
            }
            inflate(R.menu.popup_menu)
            show()
        }

    }

    private fun setValueInTVRadius(radius: String) {
        binding.tvRadius.text = resources.getString(R.string.radius, "")
        thread {
            radius.split("")
                .forEach { leter ->
                    Thread.sleep(100)
                    requireActivity().runOnUiThread {
                        binding.tvRadius.append(leter)
                    }
                }
        }
    }


    private fun showFabFromVisibilityObj() {
        binding.fab.alpha = SaveStateMapObj.alphaFab
    }


    private fun clickIvShowHideContainer() {
        binding.ivShowContainer.setOnClickListener {
            val searchFragment =
                childFragmentManager.findFragmentById(R.id.search_fragment_container)
            if (searchFragment != null) {
                showHideContainer()
            }
        }
    }

    private fun showHideContainer() {
        binding.searchFragmentContainer.isVisible =
            !binding.searchFragmentContainer.isVisible
        SaveStateMapObj.visibilityContainer = binding.searchFragmentContainer.isVisible
        changeIconIVShowHideContainer(binding.searchFragmentContainer)
    }


    private fun showHideContainerFromBundle() {
        // восстановление состояния контэйнера из бандл
        SaveStateMapObj.visibilityContainer?.let { binding.searchFragmentContainer.isVisible = it }
        changeIconIVShowHideContainer(binding.searchFragmentContainer)
    }

    private fun clickOnFab() {
        binding.fab.setOnClickListener {
            viewModelRoadLine.getDateRoadsLine(
                currentLication = SaveStateMapObj.currentLocation,
                destination = SaveStateMapObj.locationOnMapMarker
            )
        }
    }

    private fun getAllRoadsLine() {
        val waypointCoordinates = CoordinateLatLng.placeCoordinate.drop(0).take(10)
        val waypointCoordinateString = waypointCoordinates.joinToString(separator = "|")
        viewModel.getDataComplexRoute(
            originId = CoordinateLatLng.placeCoordinate[0],
            destinationId = CoordinateLatLng.placeCoordinate.last(),
            waypoints = waypointCoordinateString
        )
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
            map.getMapAsync { it.clear() }
            //разрешение на передачу данных только 1 раз
            exportData = true
            //запрос по геолокации
            if (binding.etLocation.text.isNotBlank()) {
                val location = binding.etLocation.text.toString()
                val radius = SaveStateMapObj.radius
                viewModel.getDataNearbyPlaces(location = location, radius = radius)
                //приблизить карту
                map.animationCameraMap(location)
                hideKeyboard()
            } else {
                toast(requireContext(), getString(R.string.text_not_found))
            }
        }

    }

    fun showFab() {
        if (SaveStateMapObj.currentLocation.isNotBlank() && SaveStateMapObj.locationOnMapMarker.isNotBlank()) {
            val animator = ObjectAnimator.ofFloat(binding.fab, View.ALPHA, 0f, 1f)
            animator.duration = 1000
            animator.startDelay = 1000
            val animator2 = ObjectAnimator.ofFloat(binding.fab, View.ALPHA, 0.6f, 1f)
            animator2.repeatCount = 2
            val setAnimation = AnimatorSet()
            setAnimation.play(animator).before(animator2)
            setAnimation.start()
            SaveStateMapObj.alphaFab = 1f
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

    private fun progressBarVisibilityOff() {
        (requireActivity() as GetProgressBar).getProgressBar().isVisible = false
    }

    private fun showError(error: String) {
        toast(requireContext(), error)
    }

    private fun showProgressBar() {
    }
}