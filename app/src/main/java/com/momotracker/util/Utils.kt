package com.momotracker.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Utility object containing helper functions and constants
 */
object Utils {
    
    // Date format constants
    const val DATE_FORMAT_FULL = "dd/MM/yyyy HH:mm:ss"
    const val DATE_FORMAT_SHORT = "dd/MM/yyyy"
    const val DATE_FORMAT_TIME = "HH:mm"
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val DATE_FORMAT_MONTH_YEAR = "MMMM yyyy"
    
    /**
     * Format a date to string with specified pattern
     */
    fun formatDate(date: Date, pattern: String = DATE_FORMAT_DISPLAY): String {
        return try {
            SimpleDateFormat(pattern, Locale.getDefault()).format(date)
        } catch (e: Exception) {
            date.toString()
        }
    }
    
    /**
     * Format a timestamp to string
     */
    fun formatTimestamp(timestamp: Long, pattern: String = DATE_FORMAT_DISPLAY): String {
        return formatDate(Date(timestamp), pattern)
    }
    
    /**
     * Parse string to date
     */
    fun parseDate(dateString: String, pattern: String = DATE_FORMAT_DISPLAY): Date? {
        return try {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get relative time string (e.g., "2 hours ago", "Yesterday")
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes minute${if (minutes > 1) "s" else ""} ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours hour${if (hours > 1) "s" else ""} ago"
            }
            diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days day${if (days > 1) "s" else ""} ago"
            }
            else -> formatTimestamp(timestamp, DATE_FORMAT_SHORT)
        }
    }
    
    /**
     * Format currency amount
     */
    fun formatCurrency(amount: Double, currencyCode: String = "UGX"): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "UG"))
            format.currency = java.util.Currency.getInstance(currencyCode)
            format.format(amount)
        } catch (e: Exception) {
            String.format("%.2f %s", amount, currencyCode)
        }
    }
    
    /**
     * Format number with thousand separators
     */
    fun formatNumber(number: Double): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
    }
    
    /**
     * Check if date is today
     */
    fun isToday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = timestamp
        val dateDay = calendar.get(Calendar.DAY_OF_YEAR)
        val dateYear = calendar.get(Calendar.YEAR)
        
        return today == dateDay && todayYear == dateYear
    }
    
    /**
     * Check if date is within this week
     */
    fun isThisWeek(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = timestamp
        val dateWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val dateYear = calendar.get(Calendar.YEAR)
        
        return currentWeek == dateWeek && currentYear == dateYear
    }
    
    /**
     * Check if date is within this month
     */
    fun isThisMonth(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = timestamp
        val dateMonth = calendar.get(Calendar.MONTH)
        val dateYear = calendar.get(Calendar.YEAR)
        
        return currentMonth == dateMonth && currentYear == dateYear
    }
    
    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * Get end of day timestamp
     */
    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    /**
     * Calculate percentage
     */
    fun calculatePercentage(value: Double, total: Double): Double {
        return if (total > 0) (value / total) * 100 else 0.0
    }
    
    /**
     * Validate phone number (Uganda format)
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val pattern = "^(\\+256|0)?[7][0-9]{8}$".toRegex()
        return pattern.matches(phone.replace(" ", ""))
    }
    
    /**
     * Format phone number for display
     */
    fun formatPhoneNumber(phone: String): String {
        val cleaned = phone.replace(" ", "").replace("+", "")
        return when {
            cleaned.startsWith("256") && cleaned.length == 12 -> {
                "+256 ${cleaned.substring(3, 6)} ${cleaned.substring(6, 9)} ${cleaned.substring(9)}"
            }
            cleaned.startsWith("0") && cleaned.length == 10 -> {
                "${cleaned.substring(0, 4)} ${cleaned.substring(4, 7)} ${cleaned.substring(7)}"
            }
            else -> phone
        }
    }
    
    /**
     * Generate unique ID
     */
    fun generateUniqueId(): String {
        return "${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    /**
     * Validate amount input
     */
    fun isValidAmount(amount: String): Boolean {
        return try {
            val value = amount.toDoubleOrNull()
            value != null && value > 0
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Extension functions for Context
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Extension functions for Fragment
 */
fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

fun Fragment.showSnackbar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(view, message, duration)
    if (actionText != null && action != null) {
        snackbar.setAction(actionText) { action() }
    }
    snackbar.show()
}

fun Fragment.hideKeyboard() {
    view?.let { requireContext().hideKeyboard(it) }
}

/**
 * Extension functions for View
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

/**
 * Extension functions for String
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
}

fun String.toDoubleOrZero(): Double {
    return this.toDoubleOrNull() ?: 0.0
}

fun String.toIntOrZero(): Int {
    return this.toIntOrNull() ?: 0
}

/**
 * Extension functions for Double
 */
fun Double.formatAsCurrency(currencyCode: String = "UGX"): String {
    return com.momotracker.util.Utils.formatCurrency(this, currencyCode)
}

fun Double.formatAsNumber(): String {
    return com.momotracker.util.Utils.formatNumber(this)
}

fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

/**
 * Extension functions for Long (timestamps)
 */
fun Long.toFormattedDate(pattern: String = com.momotracker.util.Utils.DATE_FORMAT_DISPLAY): String {
    return com.momotracker.util.Utils.formatTimestamp(this, pattern)
}

fun Long.toRelativeTime(): String {
    return com.momotracker.util.Utils.getRelativeTimeString(this)
}

fun Long.isToday(): Boolean {
    return com.momotracker.util.Utils.isToday(this)
}

fun Long.isThisWeek(): Boolean {
    return com.momotracker.util.Utils.isThisWeek(this)
}

fun Long.isThisMonth(): Boolean {
    return com.momotracker.util.Utils.isThisMonth(this)
}
