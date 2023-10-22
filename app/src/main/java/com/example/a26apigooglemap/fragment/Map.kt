package com.example.a26apigooglemap.fragment

import com.example.a26apigooglemap.Request.ComplexRouteResponse
import com.example.a26apigooglemap.Request.DirectionsResponse
import com.example.a26apigooglemap.Request.PlacesResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil

object CoordinateLatLng1 {
    val coordinateLatLng = mutableListOf<LatLng>()
    val placeCoordinate = mutableListOf<String>()
}
const val TEST_LOKATION1 = "50.451028649731455,30.524785575478834"

class Map : SupportMapFragment() {

    fun showMapComplexRoute(complexResponse: ComplexRouteResponse) {
        getMapAsync { googleMap ->
            val playLinePoints = complexResponse.routes[0].overview_polyline.points
            val decodePath = PolyUtil.decode(playLinePoints)
            val polygonOptions = PolylineOptions().addAll(decodePath)
            googleMap.addPolyline(polygonOptions)
            CoordinateLatLng1.coordinateLatLng.forEach { latlng ->
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
                CoordinateLatLng1.coordinateLatLng.add(coordinates)
                CoordinateLatLng1.placeCoordinate
                    .add("${results.geometry.location.lat},${results.geometry.location.lng}")
                googleMap.addMarker(MarkerOptions().position(coordinates))

            }
        }

    }


     fun animationCameraMap(locationFoZoom: String) {
        val lat = TEST_LOKATION1.substring(0, TEST_LOKATION1.indexOf(",")).toDouble()
        val lon = TEST_LOKATION1.substring(TEST_LOKATION1.indexOf(",") + 1).toDouble()
        getMapAsync { googleMap ->
            val coordinationLviv = LatLng(lat, lon)
            googleMap.addMarker(MarkerOptions().position(coordinationLviv))
//            приблизить камеру
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinationLviv, 13f))
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

}