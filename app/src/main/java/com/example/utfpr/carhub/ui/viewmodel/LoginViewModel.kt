package com.example.utfpr.carhub.ui.viewmodel

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginViewModel : ViewModel() {

    var phoneNumber by mutableStateOf("")
        private set
    var smsCode by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var verificationId by mutableStateOf<String?>(null)

    val codeSent: Boolean get() = verificationId != null

    fun onPhoneChange(value: String) { phoneNumber = value }
    fun onSmsCodeChange(value: String) { smsCode = value }
    fun clearError() { errorMessage = null }

    fun reset() {
        phoneNumber = ""
        smsCode = ""
        isLoading = false
        errorMessage = null
        verificationId = null
    }

    fun sendVerificationCode(activity: Activity, onSuccess: () -> Unit) {
        if (phoneNumber.isBlank()) {
            errorMessage = "Digite o número de telefone."
            return
        }
        isLoading = true
        val digits = phoneNumber.filter { it.isDigit() }
        val formatted = if (phoneNumber.startsWith("+")) phoneNumber else "+55$digits"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener { isLoading = false; onSuccess() }
                    .addOnFailureListener { isLoading = false; errorMessage = "Falha ao realizar login com telefone." }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                isLoading = false
                errorMessage = "Falha ao enviar código para o telefone."
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                isLoading = false
                verificationId = id
            }
        }

        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(formatted)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
        )
    }

    fun verifyCode(onSuccess: () -> Unit) {
        val id = verificationId ?: run {
            errorMessage = "Sessão expirada. Tente novamente."
            return
        }
        if (smsCode.isBlank()) {
            errorMessage = "Digite o código recebido."
            return
        }
        isLoading = true
        val credential = PhoneAuthProvider.getCredential(id, smsCode)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener { isLoading = false; onSuccess() }
            .addOnFailureListener { isLoading = false; errorMessage = "Código inválido." }
    }
}
