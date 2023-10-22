package com.example.a26apigooglemap.Request

import android.util.Log
import retrofit2.Response


class Repository(private val client: ApiClient) {
    private val apiInterface = client.retrofit.create(ApiInterface::class.java)
    suspend fun getSimpleRoutes(): Response<DirectionsResponse> {
        return apiInterface.getSimpleRoutes()
    }

    suspend fun getNearbyPlaces(location: String, radius: String): Response<PlacesResponse> {
        return apiInterface.getNearbyPlaces(location, radius)
    }

    suspend fun getComplexRoute(
        originId: String,
        destinationId: String,
        waypoints: String
    ): Response<ComplexRouteResponse> {
        return apiInterface.getComplexRoute(originId, destinationId, waypoints)
    }

}