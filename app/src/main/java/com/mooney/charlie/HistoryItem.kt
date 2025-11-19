package com.mooney.charlie

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.mooney.charlie.data.EntryType
//import com.mooney.charlie.data.FinancialEntry
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryItem(entry: FinancialEntry) {
    // Tentukan warna berdasarkan tipe
    val amountColor = when (entry.type) {
        EntryType.INCOME -> Color(0xFF388E3C) // Hijau gelap (contoh)
        EntryType.OUTCOME -> Color(0xFFD32F2F) // Merah gelap (contoh)
    }

    // Format jumlah (IDR)
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val formattedAmount = numberFormat.format(entry.amount)

    // Tentukan tanda negatif jika OUTCOME
//    val sign = if (entry.type == EntryType.OUTCOME) "-" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Kiri: Kategori dan Catatan
        Column(modifier = Modifier.weight(1f)) {
            // Kategori (Lebih besar dan warna lebih dalam/tebal)
            Text(
                text = entry.category,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Catatan (Lebih kecil dan warna lebih ringan)
            Text(
                text = entry.note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f) // Warna lebih ringan
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Kanan: Jumlah
        Text(
//            text = "$sign Rp$formattedAmount",
            text = "Rp$formattedAmount",
            color = amountColor,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
    // Opsional: Divider di bawah setiap item
    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
}