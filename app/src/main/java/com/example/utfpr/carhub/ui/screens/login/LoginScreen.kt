package com.example.utfpr.carhub.ui.screens.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utfpr.carhub.ui.viewmodel.LoginViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val activity = LocalContext.current as Activity

    viewModel.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Erro") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)) {
                        append("Car")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onSurface)) {
                        append("Hub")
                    }
                },
                fontSize = 52.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Entre com seu número de telefone",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = viewModel.phoneNumber,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Telefone (11912345678)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !viewModel.codeSent && !viewModel.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.sendVerificationCode(activity, onLoginSuccess) },
                enabled = !viewModel.codeSent && !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (viewModel.isLoading && !viewModel.codeSent) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (viewModel.codeSent) "Código enviado" else "Enviar código")
                }
            }

            AnimatedVisibility(visible = viewModel.codeSent) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = viewModel.smsCode,
                        onValueChange = { viewModel.onSmsCodeChange(it) },
                        label = { Text("Código recebido por SMS") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.verifyCode(onLoginSuccess) },
                        enabled = !viewModel.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Verificar")
                        }
                    }
                }
            }
        }
    }
}
