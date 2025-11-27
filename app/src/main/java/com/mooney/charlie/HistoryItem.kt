@file:Suppress("DEPRECATION")

package com.mooney.charlie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mooney.charlie.data.*
import com.mooney.charlie.ui.theme.CardBackgroundDark
import com.mooney.charlie.ui.theme.CardBackgroundLight
import com.mooney.charlie.ui.theme.IncomeGreen
import com.mooney.charlie.ui.theme.IncomeGreenDark
import com.mooney.charlie.ui.theme.ExpenseRed
import com.mooney.charlie.ui.theme.ExpenseRedDark
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs


// Header untuk menampilkan Tanggal dan Total Harian
// HistoryPage.kt

@Composable
fun DayHeader(date: String, totalAmount: Long) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val isDark = isSystemInDarkTheme()

    // Tentukan tanda (minus atau kosong) di sini
    val sign = if (totalAmount < 0) "-" else ""

    // Gunakan nilai absolut (abs) untuk memformat angka tanpa tanda
    val formattedTotal = numberFormat.format(abs(totalAmount))
    
    val customCardColor = if (isDark) CardBackgroundDark else CardBackgroundLight

    Surface(
        // ganti warna: using primary container for a darker/tinted look
        color = customCardColor, 
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tanggal
            Text(
                text = date,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer, // Matching content color
                modifier = Modifier.weight(1f)
            )

            // ⭐ TOTAL HARIAN YANG DIFORMAT ULANG ⭐
            Text(
                text = "$sign Rp$formattedTotal", // Tanda kini di depan Rp
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer, // Matching content color
                textAlign = TextAlign.End
            )
        }
    }
}



@Composable
fun HistoryItem(
    entry: Entry,
    onDeleteClick: (Entry) -> Unit = {}, // Make it optional for preview/
    onEditClick: (Entry) -> Unit // <--- Added Edit Callback
) {
    val isDark = isSystemInDarkTheme()
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Tentukan warna berdasarkan tipe
    val amountColor = when (entry.type) {
        EntryType.INCOME -> if (isDark) IncomeGreenDark else IncomeGreen
        EntryType.OUTCOME -> if (isDark) ExpenseRedDark else ExpenseRed
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
        // Left side: Icon/Circle that triggers menu
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { showMenu = true } // Click to show options
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                containerColor = MaterialTheme.colorScheme.background // Use screen background color
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        showMenu = false
                        onEditClick(entry)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        showMenu = false
                        showDeleteDialog = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        
        // Middle: Category and Note
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

        // Right: Amount
        Text(
//            text = "$sign Rp$formattedAmount",
            text = "Rp$formattedAmount",
            color = amountColor,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(entry)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
