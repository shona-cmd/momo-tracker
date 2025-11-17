package com.momotracker.feature.payments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momotracker.data.AirtelPaymentRepository
import com.momotracker.data.MtnMomoPaymentRepository
import com.momotracker.data.AirtelPaymentRequest
import com.momotracker.data.MtnCollectionRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentProvider {
    object MTN : PaymentProvider()
    object Airtel : PaymentProvider()
}

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val message: String, val transactionId: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val mtnRepo: MtnMomoPaymentRepository,
    private val airtelRepo: AirtelPaymentRepository
) : ViewModel() {

    var selectedProvider by mutableStateOf<PaymentProvider>(PaymentProvider.MTN)
    var phoneNumber by mutableStateOf("")
    var state by mutableStateOf<PaymentState>(PaymentState.Idle)
        private set

    fun pay5000Ugx() {
        if (phoneNumber.length < 10) {
            state = PaymentState.Error("Enter valid phone number (07xxxxxxxx)")
            return
        }

        val normalizedPhone = when (selectedProvider) {
            is PaymentProvider.MTN -> "256${phoneNumber.substring(1)}"     // 07xx → 2567xx...
            is PaymentProvider.Airtel -> "256${phoneNumber.substring(1)}"
        }

        state = PaymentState.Loading

        viewModelScope.launch {
            val result = when (selectedProvider) {
                is PaymentProvider.MTN -> mtnRepo.initiatePayment(
                    phoneNumber = normalizedPhone,
                    amount = "5000"
                )
                is PaymentProvider.Airtel -> airtelRepo.initiatePayment(
                    phoneNumber = normalizedPhone,
                    amount = "5000"
                )
            }

            state = result.fold(
                onSuccess = { response ->
                    val txId = when (selectedProvider) {
                        is PaymentProvider.MTN -> response.financialTransactionId ?: "N/A"
                        is PaymentProvider.Airtel -> response.transaction_id ?: "N/A"
                    }
                    PaymentState.Success(
                        message = "${if (selectedProvider is PaymentProvider.MTN) "MTN" else "Airtel"} Payment successful! 🎉\nPro features unlocked forever.",
                        transactionId = txId
                    )
                },
                onFailure = { e ->
                    PaymentState.Error(e.message ?: "Payment failed. Try again or send manually to 0745128746")
                }
            )
        }
    }

    fun resetState() {
        state = PaymentState.Idle
    }
}
