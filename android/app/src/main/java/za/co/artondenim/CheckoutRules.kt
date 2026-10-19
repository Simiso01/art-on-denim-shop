package za.co.artondenim

object CheckoutRules {
    val provinces = listOf(
        "Eastern Cape",
        "Free State",
        "Gauteng",
        "KwaZulu-Natal",
        "Limpopo",
        "Mpumalanga",
        "Northern Cape",
        "North West",
        "Western Cape"
    )

    val payments = listOf("payfast", "eft", "card")

    fun errors(fields: Map<String, String>): Map<String, String> {
        val problems = linkedMapOf<String, String>()
        val name = fields["full_name"].orEmpty().trim()
        val email = fields["email"].orEmpty().trim()
        val phone = fields["phone"].orEmpty().trim()
        val line = fields["address_line1"].orEmpty().trim()
        val city = fields["city"].orEmpty().trim()
        val province = fields["province"].orEmpty().trim()
        val postal = fields["postal_code"].orEmpty().trim()
        val method = fields["payment_method"].orEmpty().trim()
        if (name.isEmpty() || name.length > 160) problems["full_name"] = "Please enter your full name."
        if (!email.contains("@") || !email.contains(".")) problems["email"] = "Invalid email address."
        if (!phone.matches(Regex("^[0-9+\\-\\s()]{7,30}$"))) problems["phone"] = "Please enter a valid phone number."
        if (line.isEmpty() || line.length > 255) problems["address_line1"] = "Please enter your street address."
        if (city.isEmpty() || city.length > 100) problems["city"] = "Please enter your city."
        if (province !in provinces) problems["province"] = "Please select a valid province."
        if (!postal.matches(Regex("^[0-9A-Za-z\\-\\s]{3,20}$"))) problems["postal_code"] = "Please enter a valid postal code."
        if (method !in payments) problems["payment_method"] = "Please select a payment method."
        return problems
    }
}
