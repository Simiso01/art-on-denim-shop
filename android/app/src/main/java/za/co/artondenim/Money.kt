package za.co.artondenim

import java.text.NumberFormat
import java.util.Locale

fun formatZar(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-ZA"))
    return format.format(amount).replace("ZAR", "R").replace("\u00a0", "")
}
