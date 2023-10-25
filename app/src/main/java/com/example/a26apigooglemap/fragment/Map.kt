package com.example.a26apigooglemap.fragment

import android.util.Log
import com.example.a26apigooglemap.R
import com.example.a26apigooglemap.Request.ComplexRouteResponse
import com.example.a26apigooglemap.Request.DirectionsResponse
import com.example.a26apigooglemap.Request.PlacesResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil

object CoordinateLatLng {
    val coordinateLatLng = mutableListOf<LatLng>()
    val placeCoordinate = mutableListOf<String>()
}

class Map : SupportMapFragment(), OnMapReadyCallback {
    init {
        getMapAsync(this)
    }

    fun showMapComplexRoute(complexResponse: ComplexRouteResponse) {
        getMapAsync { googleMap ->
            val playLinePoints = complexResponse.routes[0].overview_polyline.points
            val decodePath = PolyUtil.decode(playLinePoints)
            val polygonOptions = PolylineOptions().addAll(decodePath)
            googleMap.addPolyline(polygonOptions)
            CoordinateLatLng.coordinateLatLng.forEach { latlng ->
                val coordinates = LatLng(
                    latlng.latitude,
                    latlng.longitude
                )
                googleMap.addMarker(MarkerOptions().position(coordinates))
            }
        }
    }

    fun showMapNearbyPlaces(placesResponse: PlacesResponse) {
        getMapAsync { googleMap ->
            placesResponse.results.forEach { results ->
                val coordinates = LatLng(
                    results.geometry.location.lat,
                    results.geometry.location.lng
                )
                val namePlace = results.name
                CoordinateLatLng.coordinateLatLng.add(coordinates)
                CoordinateLatLng.placeCoordinate
                    .add("${results.geometry.location.lat},${results.geometry.location.lng}")
                googleMap.addMarker(MarkerOptions().position(coordinates))
                    .apply { this?.let { title = namePlace } }
            }
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
            //TODO нужно поместить отдельно код для отображения на каарте
            // координаты
            val coordination_Lviv = LatLng(50.448416998675064, 30.51926643231341)
            googleMap.addMarker(MarkerOptions().position(coordination_Lviv))
            //приблизить камеру
            // конечная точка маршрута
            val coordinationIvanof = LatLng(48.92150140584221, 24.710288162822817)
            googleMap.addMarker(MarkerOptions().position(coordinationIvanof))

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordination_Lviv, 8f))

            val polyLine = directionsResponse.routes[0].overview_polyline.points
            val decodePath = PolyUtil.decode(polyLine)
            val polygonOptions = PolylineOptions().addAll(decodePath)
            googleMap.addPolyline(polygonOptions)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        p0.setOnMarkerClickListener {
            val searchFragment = parentFragmentManager.findFragmentById(R.id.search_fragment_container) as Marker
            searchFragment.smoothPosition(name = it.title.toString())
            false
        }
    }


}