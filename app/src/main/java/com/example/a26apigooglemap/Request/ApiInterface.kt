package com.example.a26apigooglemap.Request

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "AIzaSyDlely2vEZ0ymNqr4SLZIn54Ak5xzvoNuk"

interface ApiInterface {
    @GET("maps/api/directions/json?")
    suspend fun getSimpleRoutes(
        @Query("origin") currentLication: String,
        @Query("destination") destination: String,
        @Query("key") key: String = API_KEY
    ): Response<DirectionsResponse>


    @GET("maps/api/place/nearbysearch/json?")

    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: String,
        @Query("type") type: String = "tourist_attractions",
        @Query("key") key: String = API_KEY
    ): Response<PlacesResponse>

    @GET("maps/api/directions/json?")
    suspend fun getComplexRoute(
        @Query("origin") originId: String,
        @Query("destination") destinationId: String,
        @Query("waypoints") waypoints: String,
        @Query("key") key: String = API_KEY
    ): Response<ComplexRouteResponse>

}