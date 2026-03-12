package com.example.utfpr.carhub.ui.screens.carform

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.utfpr.carhub.model.Car
import com.example.utfpr.carhub.ui.components.CarImage
import com.example.utfpr.carhub.ui.viewmodel.AddEditCarViewModel
import com.example.utfpr.carhub.util.image.createTempCameraImageUri
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCarScreen(
    existingCar: Car?,
    viewModel: AddEditCarViewModel,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { viewModel.onImagePicked(it) } }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let(viewModel::onImagePicked)
        }
        pendingCameraUri = null
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { viewModel.onLocationSelected(it.latitude, it.longitude) }
            }
        }
    }

    val markerPosition = LatLng(viewModel.lat, viewModel.long)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 13f)
    }

    val scrollState = rememberScrollState()
    var isMapTouched by remember { mutableStateOf(false) }

    var lastInitLat by remember { mutableDoubleStateOf(0.0) }
    if (viewModel.lat != lastInitLat) {
        lastInitLat = viewModel.lat
        cameraPositionState.position = CameraPosition.fromLatLngZoom(markerPosition, 13f)
    }

    viewModel.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Campos inválidos") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
            }
        )
    }

    viewModel.deleteMessage?.let { message ->
        AlertDialog(
            onDismissRequest = {
                if (!viewModel.deleteSucceeded) {
                    viewModel.clearDeleteFeedback()
                }
            },
            title = {
                Text(if (viewModel.deleteSucceeded) "Sucesso" else "Erro")
            },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val shouldNavigate = viewModel.deleteSucceeded
                        viewModel.clearDeleteFeedback()
                        if (shouldNavigate) {
                            onDelete()
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)) {
                                append("Car")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onSurface)) {
                                append("Hub")
                            }
                        },
                        fontSize = 26.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState, enabled = !isMapTouched)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CarImage(
                    model = viewModel.previewImageModel,
                    contentDescription = "Imagem",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(6f / 3f),
                    shape = RoundedCornerShape(20.dp),
                    emptyText = "Adicione uma foto"
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Galeria")
                    }

                    OutlinedButton(
                        onClick = {
                            val uri = createTempCameraImageUri(context)
                            pendingCameraUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Camera")
                    }
                }

                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Nome do carro") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.licence,
                    onValueChange = { viewModel.onLicenceChange(it) },
                    label = { Text("Placa") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = viewModel.year,
                    onValueChange = { viewModel.onYearChange(it) },
                    label = { Text("Ano") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Localização", fontWeight = FontWeight.Medium, fontSize = 16.sp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    isMapTouched = event.changes.any { it.pressed }
                                }
                            }
                        }
                ) {
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            viewModel.onLocationSelected(latLng.latitude, latLng.longitude)
                        }
                    ) {
                        Marker(
                            state = MarkerState(position = markerPosition),
                            title = viewModel.name.ifBlank { "Carro" }
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                location?.let { viewModel.onLocationSelected(it.latitude, it.longitude) }
                            }
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Usar minha localização")
                }

                Button(
                    onClick = { viewModel.saveCar(existingCar, onSuccess = onSave) },
                    enabled = !viewModel.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewModel.isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        Text("  Enviando imagem...")
                    } else if (viewModel.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        Text("  Salvando...")
                    } else {
                        Text(if (existingCar != null) "Salvar" else "Adicionar")
                    }
                }
                if(existingCar != null) {
                    Button(
                        onClick = { viewModel.deleteCar(existingCar) },
                        enabled = !viewModel.isSaving && !viewModel.isDeleting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (viewModel.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Deletar")
                        }
                    }
                }
            }
        }
    }
}
