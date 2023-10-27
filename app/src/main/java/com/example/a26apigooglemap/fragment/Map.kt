package com.example.a26apigooglemap.fragment

import android.graphics.Color
import com.example.a26apigooglemap.R
import com.example.a26apigooglemap.Request.ComplexRouteResponse
import com.example.a26apigooglemap.Request.DirectionsResponse
import com.example.a26apigooglemap.Request.PlacesResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil

object CoordinateLatLng {
    val coordinateLatLng = mutableMapOf<String, LatLng>()
    val placeCoordinate = mutableListOf<String>()
}

class Map : SupportMapFragment() {


    fun showMapComplexRoute(complexResponse: ComplexRouteResponse) {
        getMapAsync { googleMap ->
            val playLinePoints = complexResponse.routes[0].overview_polyline.points
            val decodePath = PolyUtil.decode(playLinePoints)
            val polygonOptions = PolylineOptions().addAll(decodePath)
            googleMap.addPolyline(polygonOptions)
            CoordinateLatLng.coordinateLatLng.forEach { mapSL ->

                googleMap.addMarker(MarkerOptions().position(mapSL.value))
            }
        }
    }

    fun showMapNearbyPlaces(placesResponse: PlacesResponse) {
        addValueInCoordinateLatLng(placesResponse)
        showPlaceMarkerOnMap()
    }

    private fun showPlaceMarkerOnMap() {
        getMapAsync { googleMap ->
            CoordinateLatLng.coordinateLatLng.forEach { mapSL ->
                googleMap.addMarker(MarkerOptions().position(mapSL.value)).apply {
                    this?.let { title = mapSL.key }
                }
            }
        }
    }

    private fun addValueInCoordinateLatLng(placesResponse: PlacesResponse) {
        placesResponse.results.forEach { results ->
            // мап имя и координвты
            CoordinateLatLng.coordinateLatLng
                .put(
                    results.name, (LatLng(
                        results.geometry.location.lat,
                        results.geometry.location.lng
                    ))
                )
            // данные для построения множества дорог
            CoordinateLatLng.placeCoordinate
                .add("${results.geometry.location.lat},${results.geometry.location.lng}")
        }
    }


    fun animationCameraMap(locationFoZoom: String, zoom: Float = 13f, namePlace: String = "") {
        val lat = locationFoZoom.substring(0, locationFoZoom.indexOf(",")).toDouble()
        val lon = locationFoZoom.substring(locationFoZoom.indexOf(",") + 1).toDouble()
        getMapAsync { googleMap ->
            val currentLocation = LatLng(lat, lon)
            googleMap.addMarker(MarkerOptions().position(currentLocation))?.let {
                it.title = namePlace
                it.showInfoWindow()
            }
//            приблизить камеру
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
        }
    }


    fun showMapBuildLine(directionsResponse: DirectionsResponse) {
        getMapAsync { googleMap ->
            //TODO работает но нужно переделать
            //________________________________________________
            googleMap.clear()

            showPlaceMarkerOnMap()
            animationCameraMap(
                SaveStateMapObj.currentLocation,
                namePlace = resources.getString(R.string.You)
            )
            //______________________________________________________________

            val polyLine = directionsResponse.routes[0].overview_polyline.points
            val decodePath = PolyUtil.decode(polyLine)
            val polygonOptions = PolylineOptions()
            polygonOptions.addAll(decodePath)
            googleMap.addPolyline(polygonOptions).color = Color.CYAN
        }
    }


}