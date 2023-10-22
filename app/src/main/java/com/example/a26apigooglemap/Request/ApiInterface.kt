package com.example.a26apigooglemap.Request

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "AIzaSyDlely2vEZ0ymNqr4SLZIn54Ak5xzvoNuk"

interface ApiInterface {
    @GET(
        "maps/api/directions/json?" +
                "&origin=50.448416998675064,30.51926643231341" +
                "&destination=48.92150140584221,24.710288162822817" +
                "&key=$API_KEY"
    )
    suspend fun getSimpleRoutes(): Response<DirectionsResponse>

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