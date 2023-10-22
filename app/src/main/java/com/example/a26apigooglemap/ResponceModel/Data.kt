package com.example.a26apigooglemap.Request

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class ComplexRouteResponse(val routes: List<Routes>)

data class DirectionsResponse(val routes: List<Routes>)
data class Routes(val overview_polyline: OverviewPolyline)
data class OverviewPolyline(val points: String)

data class PlacesResponse(val results: List<Results>) : Serializable
data class Results(val geometry: Geometry, val photos: List<Photos>, val name: String)
data class Geometry(val location: Location)
data class Location(val lat: Double, val lng: Double)
data class Photos(val photo_reference: String? = null)

