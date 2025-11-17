package com.momotracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun PaymentScreen(viewModel: PaymentViewModel = hiltViewModel()) {
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val paymentStatus by viewModel.paymentStatus.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Airtel Payment",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Airtel Phone (07xx...)") }
        )
        Button(
            onClick = {
                isLoading = true
                viewModel.processAirtelPayment(phone,
                    onSuccess = { success ->
                        // Navigate to download or unlock pro features
                        isLoading = false
                    },
                    onError = { error ->
                        // Show Snackbar
                        isLoading = false
                    }
                )
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Processing..." else "Pay 5,000 UGX via Airtel")
        }

        paymentStatus?.let {
            Text(text = it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen()
}
