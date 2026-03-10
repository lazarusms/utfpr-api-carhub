package com.example.utfpr.carhub.data.repository

import com.example.utfpr.carhub.core.common.Result
import com.example.utfpr.carhub.core.common.safeApiCall
import com.example.utfpr.carhub.data.network.RetrofitClient
import com.example.utfpr.carhub.model.Car
import com.example.utfpr.carhub.model.CarRequest

class CarRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getCars(): Result<List<Car>> = safeApiCall { apiService.getCars() }

    suspend fun addCar(request: CarRequest): Result<Car> = safeApiCall { apiService.addCar(request) }

    suspend fun updateCar(id: String, request: CarRequest): Result<Car> = safeApiCall { apiService.updateCar(id, request) }

    suspend fun deleteCar(id: String): Result<Unit> = safeApiCall { apiService.deleteCar(id) }
}
