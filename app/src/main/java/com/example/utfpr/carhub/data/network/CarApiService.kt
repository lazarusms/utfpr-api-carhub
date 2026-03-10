package com.example.utfpr.carhub.data.network

import com.example.utfpr.carhub.model.Car
import com.example.utfpr.carhub.model.CarRequest

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CarApiService {

    @GET("car")
    suspend fun getCars(): List<Car>

//    @GET("car/{id}")
//    suspend fun getCar(@Path("id") id: String): Car

    @DELETE("car/{id}")
    suspend fun deleteCar(@Path("id") id: String)

    @PATCH("car/{id}")
    suspend fun updateCar(@Path("id") id: String, @Body item: CarRequest): Car

    @POST("car")
    suspend fun addCar(@Body item: CarRequest): Car

}
