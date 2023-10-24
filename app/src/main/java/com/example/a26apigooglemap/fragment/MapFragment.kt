package com.example.a26apigooglemap.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a26apigooglemap.R
import com.example.a26apigooglemap.Request.ComplexRouteResponse
import com.example.a26apigooglemap.Request.DirectionsResponse
import com.example.a26apigooglemap.Request.PlacesResponse
import com.example.a26apigooglemap.databinding.MapFragmentBinding
import com.example.a26apigooglemap.progressBar
import com.example.a26apigooglemap.toast
import com.example.a26apigooglemap.viewModel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread

object SaveStateMapObj {
    var visibilityContainer: Boolean? = null
    var alphaFab = 0f
    var radius = "2000"
}

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
        progressBar.isVisible = false
        initField()
        clickOnSearch()
        clickOnFab()
        clickOnRadius()
        clickIvShowHideContainer()
        showHideContainerFromBundle()
        showFabFromVisibilityObj()
        setValueInTVRadius(SaveStateMapObj.radius)
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
        binding.tvRadius.text = resources.getString(R.string.radius,"")
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

    private fun initField() {
        childFragmentManager.findFragmentById(R.id.map_fragment_container).let { map = it as Map }
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]

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
        binding.fab.setOnClickListener {

            val waypointCoordinates = CoordinateLatLng.placeCoordinate.drop(0).take(10)
            val waypointCoordinateString = waypointCoordinates.joinToString(separator = "|")
            viewModel.getDataComplexRoute(
                originId = CoordinateLatLng.placeCoordinate[0],
                destinationId = CoordinateLatLng.placeCoordinate.last(),
                waypoints = waypointCoordinateString
            )
        }
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
            //запрос по геолокации
            if (binding.etLocation.text.isNotBlank()) {
                val location = binding.etLocation.text.toString()
                val radius = SaveStateMapObj.radius
                viewModel.getDataNearbyPlaces(location = location, radius = radius)
                //приблизить карту
                map.animationCameraMap(location)
                hideKeyboard()
                showFab()
            } else {
                toast(requireContext(), getString(R.string.text_not_found))
            }
        }

    }

    private fun showFab() {
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