package za.co.artondenim.data

data class ProductCard(
    val id: Int,
    val name: String,
    val brand: String,
    val price: Double,
    val image: String
)

data class SizeOption(val size: String, val inStock: Boolean)

data class ProductDetail(
    val id: Int,
    val name: String,
    val brand: String,
    val price: Double,
    val image: String,
    val description: String,
    val sizes: List<SizeOption>,
    val related: List<ProductCard>
)

data class CartLine(
    val id: Int,
    val name: String,
    val size: String,
    val quantity: Int,
    val lineTotal: Double
)

data class CartSnapshot(
    val items: List<CartLine>,
    val subtotal: Double,
    val discount: Double,
    val coupon: String?,
    val shipping: Double,
    val total: Double
)

data class OrderSummary(
    val number: String,
    val status: String,
    val total: Double,
    val placedAt: String
)

data class TimelineEvent(val label: String, val note: String, val at: String)

data class OrderDetail(
    val number: String,
    val status: String,
    val paymentStatus: String,
    val total: Double,
    val tracking: String,
    val carrier: String,
    val lines: List<String>,
    val timeline: List<TimelineEvent>
)

data class Note(val id: Int, val title: String, val message: String, val read: Boolean)

data class JournalCard(val title: String, val slug: String, val excerpt: String)

data class JournalPost(val title: String, val body: String, val author: String)

data class Account(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val line1: String,
    val city: String,
    val province: String,
    val postal: String,
    val role: String = "customer"
)

data class NamedSlug(val name: String, val slug: String)

data class CatalogueMeta(
    val brands: List<NamedSlug>,
    val categories: List<NamedSlug>,
    val sizes: List<String>,
    val google: Boolean
)

data class AdminDash(
    val sales: Double,
    val ordersToday: Int,
    val ordersTotal: Int,
    val customers: Int,
    val products: Int,
    val lowStock: Int,
    val messages: Int,
    val orders: List<String>,
    val stock: List<String>
)
