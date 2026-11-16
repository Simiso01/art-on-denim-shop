package za.co.artondenim

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {
    @Test
    fun formatsSouthAfricanRand() {
        val formatted = formatZar(1899.0)
        assertEquals(true, formatted.contains("1") && formatted.contains("899"))
    }

    @Test
    fun checkoutRejectsAnEmptyOrder() {
        val problems = CheckoutRules.errors(emptyMap())
        assertEquals(true, problems.containsKey("full_name") && problems.containsKey("province"))
    }

    @Test
    fun checkoutAcceptsAGautengAddress() {
        val problems = CheckoutRules.errors(
            mapOf(
                "full_name" to "Lebo Mokoena",
                "email" to "lebo@example.com",
                "phone" to "0820000002",
                "address_line1" to "12 Kloof Street",
                "city" to "Cape Town",
                "province" to "Western Cape",
                "postal_code" to "8001",
                "payment_method" to "payfast"
            )
        )
        assertEquals(0, problems.size)
    }
}
