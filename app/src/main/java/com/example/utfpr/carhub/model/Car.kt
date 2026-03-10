package com.example.utfpr.carhub.model

data class Car(
    val id: String,
    val name: String,
    val licence: String,
    val imageUrl: String,
    val year: String,
    val place: CarLocation
)

data class CarLocation(
    val lat: Double,
    val long: Double
)

data class CarRequest(
    val id: String,
    val name: String,
    val licence: String,
    val imageUrl: String,
    val year: String,
    val place: CarLocation
)
