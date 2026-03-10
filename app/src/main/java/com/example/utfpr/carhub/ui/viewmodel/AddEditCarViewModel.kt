package com.example.utfpr.carhub.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utfpr.carhub.core.common.Result
import com.example.utfpr.carhub.data.repository.CarRepository
import com.example.utfpr.carhub.data.repository.StorageRepository
import com.example.utfpr.carhub.model.Car
import com.example.utfpr.carhub.model.CarLocation
import com.example.utfpr.carhub.model.CarRequest
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditCarViewModel : ViewModel() {
    private val carRepository = CarRepository()
    private val storageRepository = StorageRepository()

    var id by mutableStateOf("")
        private set
    var name by mutableStateOf("")
        private set
    var licence by mutableStateOf("")
        private set
    var year by mutableStateOf("")
        private set
    var imageUrl by mutableStateOf("")
        private set
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set
    var lat by mutableDoubleStateOf(0.0)
        private set
    var long by mutableDoubleStateOf(0.0)
        private set
    var isUploading by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var isDeleting by mutableStateOf(false)
        private set
    var deleteSucceeded by mutableStateOf(false)
        private set
    var deleteMessage by mutableStateOf<String?>(null)
        private set
    val previewImageModel: Any?
        get() = selectedImageUri ?: imageUrl.takeIf { it.isNotBlank() }

    fun initFromCar(car: Car?) {
        id = car?.id ?: ""
        name = car?.name ?: ""
        licence = car?.licence ?: ""
        year = car?.year ?: ""
        imageUrl = car?.imageUrl ?: ""
        lat = car?.place?.lat ?: 0.0
        long = car?.place?.long ?: 0.0
        selectedImageUri = null
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onLicenceChange(value: String) {
        licence = value
    }

    fun onYearChange(value: String) {
        year = value
    }

    fun onImagePicked(uri: Uri) {
        selectedImageUri = uri
    }

    fun onLocationSelected(newLat: Double, newLong: Double) {
        lat = newLat
        long = newLong
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            isDeleting = true
            deleteSucceeded = false
            deleteMessage = null
            val result = carRepository.deleteCar(car.id)
            if (result is Result.Success) {
                deleteSucceeded = true
                deleteMessage = "Carro deletado com sucesso."
            } else if (result is Result.Error) {
                deleteMessage = result.message.ifBlank { "Falha ao deletar carro." }
            }
            isDeleting = false
        }
    }

    fun clearDeleteFeedback() {
        deleteSucceeded = false
        deleteMessage = null
    }

    fun saveCar(existingCar: Car?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            val finalImageUrl = selectedImageUri?.let { uri ->
                isUploading = true
                val result = storageRepository.uploadImage(uri)
                isUploading = false
                (result as? Result.Success)?.data ?: imageUrl
            } ?: imageUrl

            val request = CarRequest(
                id = existingCar?.id ?: id.ifBlank { UUID.randomUUID().toString() },
                name = name,
                licence = licence,
                year = year,
                imageUrl = finalImageUrl,
                place = CarLocation(lat, long)
            )

            val result = if (existingCar != null) {
                carRepository.updateCar(existingCar.id, request)
            } else {
                carRepository.addCar(request)
            }

            isSaving = false
            if (result is Result.Success) onSuccess()
        }
    }
}
