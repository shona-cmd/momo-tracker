package com.momotracker.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import com.momotracker.data.MtnMomoPaymentRepository
import com.momotracker.data.MtnAuthRequest
import com.momotracker.data.MtnCollectionRequest
import com.momotracker.data.MtnPayer
import java.util.UUID

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: MtnMomoPaymentRepository
) : ViewModel() {

    private val _paymentStatus = MutableStateFlow<String?>(null)
    val paymentStatus: StateFlow<String?> = _paymentStatus.asStateFlow()

    fun processAirtelPayment(
        phone: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _paymentStatus.value = "Processing..."
            paymentRepository.initiatePayment(phone)
                .onSuccess {
                    _paymentStatus.value = "Payment Successful"
                    onSuccess(true)
                }
                .onFailure {
                    _paymentStatus.value = "Payment Failed: ${it.message}"
                    onError(it.message ?: "Payment failed")
                }
        }
    }
}
