package com.example.utfpr.carhub.data.repository

import android.net.Uri
import com.example.utfpr.carhub.core.common.Result
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {
    private val storage = Firebase.storage

    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child("images/car_${UUID.randomUUID()}")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error(-1, e.message ?: "Falha ao fazer upload da imagem")
        }
    }
}
