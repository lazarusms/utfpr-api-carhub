package com.example.utfpr.carhub.util.image

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun createTempCameraImageUri(context: Context): Uri {
    val imageFile = File.createTempFile(
        "carhub_camera_",
        ".jpg",
        context.cacheDir
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}
