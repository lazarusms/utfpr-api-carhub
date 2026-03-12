package com.example.utfpr.carhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utfpr.carhub.core.common.Result
import com.example.utfpr.carhub.data.repository.CarRepository
import com.example.utfpr.carhub.model.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = CarRepository()

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCars()
    }

    fun loadCars() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCars()
            if (result is Result.Success) {
                _cars.value = result.data.sortedBy { it.id }//ordena pelo id
            }
            _isLoading.value = false
        }
    }

//    fun deleteCar(car: Car) {
//        viewModelScope.launch {
//            repository.deleteCar(car.id)
//            loadCars()
//        }
//    }
}
