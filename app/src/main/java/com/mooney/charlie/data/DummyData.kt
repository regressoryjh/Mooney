package com.mooney.charlie.data

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

// 1. Data Class Definition
data class FinancialEntry(
    val id: String = UUID.randomUUID().toString(), // Unique ID for list keys
    val type: EntryType, // Income or Outcome
    val category: String,
    val date: String, // YYYY-MM-DD
    val amount: Long, // Use Long for currency to avoid floating point issues
    val note: String
)

enum class EntryType {
    INCOME,
    OUTCOME
}

// 2. Formatting Helpers
// Use Indonesian Rupiah format
private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
    maximumFractionDigits = 0
}

/** Formats a Long amount into Indonesian Rupiah (e.g., "Rp 15.000.000") */
fun formatRupiah(amount: Long): String {
    return currencyFormat.format(amount.absoluteValue)
}

/** Gets the current date formatted as YYYY-MM-DD */
fun getCurrentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

/** Gets the current month and year formatted as YYYY-MM */
fun getCurrentMonthYear(): String {
    return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
}

// 3. Dummy Data List
val BudgetEntries = listOf(
    FinancialEntry(
        type = EntryType.INCOME,
        category = "Salary",
        date = "2025-10-01",
        amount = 5_000_000,
        note = "Monthly Salary Deposit"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-01",
        amount = 45_000,
        note = "Lunch at Warung Padang"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-10-01",
        amount = 12_000,
        note = "Gojek ride to office"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Household",
        date = "2025-10-02",
        amount = 150_000,
        note = "Detergent and cleaning supplies"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-02",
        amount = 35_000,
        note = "Coffee and pastry"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Entertainment",
        date = "2025-10-03",
        amount = 120_000,
        note = "Cinema ticket"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-10-03",
        amount = 25_000,
        note = "Taxi home late night"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Bills & Utilities",
        date = "2025-10-04",
        amount = 350_000,
        note = "Electricity bill (October)"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-04",
        amount = 60_000,
        note = "Dinner with friends"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Personal Care",
        date = "2025-10-05",
        amount = 250_000,
        note = "Haircut and grooming"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-10-05",
        amount = 18_000,
        note = "GrabBike trip"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-06",
        amount = 40_000,
        note = "Street food snack"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-10-07",
        amount = 450_000,
        note = "New shirt"
    ),
    FinancialEntry(
        type = EntryType.INCOME,
        category = "Freelance",
        date = "2025-10-08",
        amount = 3_500_000,
        note = "Payment for web project"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Investment",
        date = "2025-10-08",
        amount = 1_000_000,
        note = "Buy mutual funds"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-09",
        amount = 85_000,
        note = "Weekend brunch"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Bills & Utilities",
        date = "2025-10-10",
        amount = 100_000,
        note = "Prepaid mobile data top-up"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-10",
        amount = 22_000,
        note = "Quick breakfast"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-10-11",
        amount = 15_000,
        note = "Bus fare"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Household",
        date = "2025-10-12",
        amount = 80_000,
        note = "Groceries (vegetables, eggs)"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Entertainment",
        date = "2025-10-12",
        amount = 50_000,
        note = "Streaming service subscription"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-13",
        amount = 48_000,
        note = "Late dinner delivery"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Bills & Utilities",
        date = "2025-10-14",
        amount = 800_000,
        note = "Monthly apartment rent"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-15",
        amount = 75_000,
        note = "Team lunch treat"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-10-15",
        amount = 10_000,
        note = "Angkot ride"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-10-16",
        amount = 1_200_000,
        note = "New running shoes"
    ),
    FinancialEntry(
        type = EntryType.INCOME,
        category = "Interest",
        date = "2025-10-17",
        amount = 25_000,
        note = "Savings account interest"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Education",
        date = "2025-10-18",
        amount = 400_000,
        note = "Online course subscription"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-18",
        amount = 30_000,
        note = "Nasi Goreng"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Personal Care",
        date = "2025-10-19",
        amount = 15_000,
        note = "Toothpaste replacement"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Entertainment",
        date = "2025-10-20",
        amount = 250_000,
        note = "Concert ticket down payment"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Household",
        date = "2025-10-21",
        amount = 95_000,
        note = "Cooking oil and rice"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-10-22",
        amount = 16_000,
        note = "GoCar split fare"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-23",
        amount = 55_000,
        note = "Pizza slice treat"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-10-24",
        amount = 200_000,
        note = "Phone case replacement"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Bills & Utilities",
        date = "2025-10-25",
        amount = 180_000,
        note = "Internet/WiFi bill"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-26",
        amount = 90_000,
        note = "Special weekend dinner"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Health",
        date = "2025-10-27",
        amount = 150_000,
        note = "Vitamins and supplements"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-10-28",
        amount = 14_000,
        note = "Morning ride to work"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-10-29",
        amount = 38_000,
        note = "Delivery fee for groceries"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Personal Care",
        date = "2025-10-30",
        amount = 60_000,
        note = "New facial wash"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Entertainment",
        date = "2025-10-31",
        amount = 75_000,
        note = "Arcade games night"
    ),
    FinancialEntry(
        type = EntryType.INCOME,
        category = "Salary",
        date = "2025-11-01",
        amount = 5_000_000,
        note = "Monthly Salary Deposit"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-01",
        amount = 55_000,
        note = "First lunch of the month"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-11-02",
        amount = 15_000,
        note = "Gojek to meeting"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Household",
        date = "2025-11-02",
        amount = 120_000,
        note = "New light bulbs"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Bills & Utilities",
        date = "2025-11-03",
        amount = 380_000,
        note = "Electricity bill (November)"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-03",
        amount = 110_000,
        note = "Sushi dinner"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-11-04",
        amount = 300_000,
        note = "Small gift for coworker"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Investment",
        date = "2025-11-05",
        amount = 1_500_000,
        note = "Monthly stock purchase"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-06",
        amount = 42_000,
        note = "Mid-day snack"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-11-07",
        amount = 20_000,
        note = "Return trip by MRT"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Entertainment",
        date = "2025-11-08",
        amount = 180_000,
        note = "Dinner and karaoke"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Personal Care",
        date = "2025-11-09",
        amount = 75_000,
        note = "Manicure at home"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-10",
        amount = 65_000,
        note = "Sunday market food haul"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Household",
        date = "2025-11-11",
        amount = 50_000,
        note = "New dish soap"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Bills & Utilities",
        date = "2025-11-12",
        amount = 120_000,
        note = "Water refill and laundry"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-13",
        amount = 33_000,
        note = "Quick office lunch"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-11-14",
        amount = 12_000,
        note = "Morning Gojek"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-11-15",
        amount = 800_000,
        note = "Replacement shoes for work"
    ),
    FinancialEntry(
        type = EntryType.INCOME,
        category = "Bonus",
        date = "2025-11-16",
        amount = 1_000_000,
        note = "Project completion bonus"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Health",
        date = "2025-11-16",
        amount = 500_000,
        note = "Doctor visit (check-up)"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Entertainment",
        date = "2025-11-17",
        amount = 60_000,
        note = "Download new video game"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-18",
        amount = 40_000,
        note = "Bakso stall dinner"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Personal Care",
        date = "2025-11-19",
        amount = 100_000,
        note = "Lotion and perfume"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-11-20",
        amount = 18_000,
        note = "Late-night transport"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-11-21",
        amount = 150_000,
        note = "Books purchase"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Education",
        date = "2025-11-22",
        amount = 150_000,
        note = "E-book on finance"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-23",
        amount = 70_000,
        note = "Takeaway dinner"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Bills & Utilities",
        date = "2025-11-24",
        amount = 200_000,
        note = "Cable TV and Internet upgrade"
    ),

    // ⭐ Today's entries (2025-11-25) - This will be displayed in Recent Transactions
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Household",
        date = "2025-11-25",
        amount = 70_000,
        note = "Weekly cleaning service tip"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Household",
        date = "2025-11-25",
        amount = 70_000,
        note = "Weekly cleaning service tip"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-25",
        amount = 52_000,
        note = "Lunch and a drink"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-11-25",
        amount = 10_000,
        note = "Public transport ticket"
    ),
    // End of Today's entries (2025-11-25)
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-11-28",
        amount = 950_000,
        note = "Clothing for an event"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-29",
        amount = 180_000,
        note = "Fancy restaurant dinner"
    ),
    FinancialEntry(
        type = EntryType.INCOME,
        category = "Freelance",
        date = "2025-11-30",
        amount = 1_000_000,
        note = "Small consulting payment"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-26",
        amount = 52_000,
        note = "Lunch and a drink"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Transport",
        date = "2025-11-27",
        amount = 10_000,
        note = "Public transport ticket"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Shopping",
        date = "2025-11-28",
        amount = 950_000,
        note = "Clothing for an event"
    ),
    FinancialEntry(
        type = EntryType.OUTCOME,
        category = "Food & Drink",
        date = "2025-11-29",
        amount = 180_000,
        note = "Fancy restaurant dinner"
    ),
    FinancialEntry(
        type = EntryType.INCOME,
        category = "Freelance",
        date = "2025-11-30",
        amount = 1_000_000,
        note = "Small consulting payment"
    )
)

// Add On - Keeping the list structures for future use (e.g., charts)
val dailyExpensesList: List<Pair<String, Float>> =
    BudgetEntries.groupBy { it.date }
        .map { entry ->
            val total = entry.value.sumOf { it.amount }
            entry.key to total.toFloat()
        }
        .sortedBy { it.first }

val monthlyExpensesList: List<Pair<String, Float>> =
    BudgetEntries.groupBy { it.date.substring(0, 7) } // YYYY-MM
        .map { entry ->
            val total = entry.value.sumOf { it.amount }
            entry.key to total.toFloat()
        }
        .sortedBy { it.first }

val yearlyExpensesList: List<Pair<String, Float>> =
    BudgetEntries.groupBy { it.date.substring(0, 4) } // YYYY
        .map { entry ->
            val total = entry.value.sumOf { it.amount }
            entry.key to total.toFloat()
        }
        .sortedBy { it.first }