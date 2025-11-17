package com.momotracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CardDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.momotracker.feature.payments.PaymentProvider
import com.momotracker.feature.payments.PaymentState
import com.momotracker.feature.payments.PaymentViewModel

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Unlock Momo Tracker Pro", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        // Provider Selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = viewModel.selectedProvider is PaymentProvider.MTN,
                onClick = { viewModel.selectedProvider = PaymentProvider.MTN }
            )
            Text("MTN MoMo", fontSize = 18.sp)

            RadioButton(
                selected = viewModel.selectedProvider is PaymentProvider.Airtel,
                onClick = { viewModel.selectedProvider = PaymentProvider.Airtel }
            )
            Text("Airtel Money", fontSize = 18.sp)
        }

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.phoneNumber = it },
            label = { Text("Phone number (07xxxxxxxx)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { viewModel.pay5000Ugx() },
            enabled = state !is PaymentState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state is PaymentState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text("Processing ${if (viewModel.selectedProvider is PaymentProvider.MTN) "MTN" else "Airtel"}...")
            } else {
                Text("Pay 5,000 UGX → Unlock Forever", fontSize = 18.sp)
            }
        }

        when (state) {
            is PaymentState.Success -> {
                Spacer(Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (state as PaymentState.Success).message,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                // Auto-unlock Pro (save flag)
                LaunchedEffect(Unit) {
                    // Save to EncryptedSharedPrefs or DataStore
                    // proUnlocked = true
                }
            }
            is PaymentState.Error -> {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = (state as PaymentState.Error).message,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
            else -> {}
        }
    }
}
