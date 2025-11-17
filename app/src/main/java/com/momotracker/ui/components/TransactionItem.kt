package com.momotracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.momotracker.data.Transaction

@Composable
fun TransactionItem(tx: Transaction) {
    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tx.type == "DEBIT") Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("UGX ${tx.amount}", style = MaterialTheme.typography.titleMedium)
            Text(tx.phone, style = MaterialTheme.typography.bodySmall)
            Row {
                Text("Ref: ${tx.ref}", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(8.dp))
                Badge(containerColor = categoryColor(tx.category)) {
                    Text(tx.category, color = Color.White)
                }
            }
        }
    }
}

fun categoryColor(cat: String) = when (cat) {
    "Food" -> Color(0xFFF57C00)
    "Transport" -> Color(0xFF1976D2)
    "School Fees" -> Color(0xFF388E3C)
    "Savings" -> Color(0xFF7B1FA2)
    else -> Color.Gray
}
