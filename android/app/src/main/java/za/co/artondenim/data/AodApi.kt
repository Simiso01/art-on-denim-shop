package za.co.artondenim.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class AodApi(private val baseUrl: String, private val session: SessionStore) {
    private val jsonType = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun login(email: String, password: String): Account {
        val data = post("auth.php?action=login", json("email" to email, "password" to password, "device_name" to "Art on Denim Android")).obj("data")
        session.saveToken(data.getString("token"))
        return accountFrom(data.obj("user"), JSONObject())
    }

    fun register(first: String, last: String, email: String, password: String): Account {
        val data = post(
            "auth.php?action=register",
            json(
                "first_name" to first,
                "last_name" to last,
                "email" to email,
                "password" to password,
                "password_confirm" to password,
                "device_name" to "Art on Denim Android"
            )
        ).obj("data")
        session.saveToken(data.getString("token"))
        return accountFrom(data.obj("user"), JSONObject())
    }

    fun logout() {
        try {
            post("auth.php?action=logout", JSONObject())
        } finally {
            session.clear()
        }
    }

    fun exchangeGoogle(code: String): Account {
        val data = post("auth.php?action=google_handoff", json("code" to code, "device_name" to "Art on Denim Android")).obj("data")
        session.saveToken(data.getString("token"))
        return accountFrom(data.obj("user"), JSONObject())
    }

    fun me(): Account {
        val data = get("auth.php?action=me").obj("data")
        return accountFrom(data.obj("user"), data.optJSONObject("profile") ?: JSONObject())
    }

    fun saveProfile(account: Account) {
        post(
            "auth.php?action=profile",
            json(
                "first_name" to account.firstName,
                "last_name" to account.lastName,
                "phone" to account.phone,
                "address_line1" to account.line1,
                "city" to account.city,
                "province" to account.province,
                "postal_code" to account.postal
            )
        )
    }

    fun meta(): CatalogueMeta {
        val data = get("products.php?action=meta").obj("data")
        val sizes = data.optJSONArray("sizes")
        return CatalogueMeta(
            brands = data.arr("brands").named(),
            categories = data.arr("categories").named(),
            sizes = if (sizes == null) emptyList() else List(sizes.length()) { sizes.optString(it) },
            google = data.optBoolean("google")
        )
    }

    fun adminLines(action: String): List<String> {
        val items = get("admin.php?action=$action").obj("data").arr("items")
        return List(items.length()) {
            val row = items.getJSONObject(it)
            if (action == "customers") {
                "${row.optString("first_name")} ${row.optString("last_name")} · ${row.optString("email")} · ${row.optString("city")}"
            } else {
                "${row.optString("name")} · ${row.optString("subject")} · ${row.optString("status")}"
            }
        }
    }

    fun admin(): AdminDash {
        val data = get("admin.php?action=dashboard").obj("data")
        val stats = data.obj("stats")
        val orders = data.arr("orders")
        val stock = data.arr("low_stock")
        return AdminDash(
            sales = stats.optDouble("sales_total"),
            ordersToday = stats.optInt("orders_today"),
            ordersTotal = stats.optInt("orders_total"),
            customers = stats.optInt("customers"),
            products = stats.optInt("products"),
            lowStock = stats.optInt("low_stock"),
            messages = stats.optInt("new_messages"),
            orders = List(orders.length()) {
                val row = orders.getJSONObject(it)
                "${row.optString("order_number")} · ${row.optString("email")} · ${row.optString("status")} · ${row.optDouble("total")}"
            },
            stock = List(stock.length()) {
                val row = stock.getJSONObject(it)
                "${row.optString("name")} ${row.optString("size")} · ${row.optInt("quantity")} left"
            }
        )
    }

    fun products(query: Map<String, String> = emptyMap()): List<ProductCard> {
        val extra = query.filterValues { it.isNotBlank() }.entries.joinToString("&") {
            "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}"
        }
        val path = if (extra.isEmpty()) "products.php?per_page=24" else "products.php?per_page=24&$extra"
        return get(path).obj("data").arr("items").products()
    }

    fun product(id: Int): ProductDetail {
        val data = get("products.php?id=$id").obj("data")
        val sizes = data.optJSONArray("sizes")
        val related = data.optJSONArray("related")
        return ProductDetail(
            id = data.getInt("id"),
            name = data.optString("name"),
            brand = data.optString("brand"),
            price = data.optDouble("price"),
            image = data.optString("image"),
            description = data.optString("description"),
            sizes = if (sizes == null) emptyList() else List(sizes.length()) {
                val row = sizes.getJSONObject(it)
                SizeOption(row.optString("size"), row.optBoolean("in_stock"))
            },
            related = related?.products() ?: emptyList()
        )
    }

    fun cart(): CartSnapshot {
        val data = get("cart.php").obj("data")
        val items = data.arr("items")
        return CartSnapshot(
            items = List(items.length()) {
                val row = items.getJSONObject(it)
                CartLine(row.getInt("id"), row.optString("name"), row.optString("size"), row.optInt("quantity"), row.optDouble("line_total"))
            },
            subtotal = data.optDouble("subtotal"),
            discount = data.optDouble("discount"),
            coupon = data.optString("coupon").takeIf { it.isNotBlank() && it != "null" },
            shipping = data.optDouble("shipping"),
            total = data.optDouble("total")
        )
    }

    fun addToCart(productId: Int, size: String) {
        post("cart.php", json("product_id" to productId, "size" to size, "quantity" to 1))
    }

    fun updateCart(itemId: Int, quantity: Int) {
        send("PUT", "cart.php", json("item_id" to itemId, "quantity" to quantity))
    }

    fun removeCart(itemId: Int) {
        send("DELETE", "cart.php", json("item_id" to itemId))
    }

    fun applyCoupon(code: String) {
        post("cart.php?action=coupon", json("coupon_code" to code))
    }

    fun removeCoupon() {
        post("cart.php?action=coupon_remove", JSONObject())
    }

    fun wishlist(): List<ProductCard> = get("wishlist.php").obj("data").arr("items").products()

    fun saveWish(productId: Int) {
        post("wishlist.php", json("product_id" to productId))
    }

    fun removeWish(productId: Int) {
        send("DELETE", "wishlist.php", json("product_id" to productId))
    }

    fun orders(): List<OrderSummary> {
        val items = get("orders.php").obj("data").arr("items")
        return List(items.length()) {
            val row = items.getJSONObject(it)
            OrderSummary(row.optString("order_number"), row.optString("status_label"), row.optDouble("total"), row.optString("placed_at"))
        }
    }

    fun order(number: String): OrderDetail {
        val data = get("orders.php?number=${URLEncoder.encode(number, "UTF-8")}").obj("data")
        val order = data.obj("order")
        val items = data.arr("items")
        val timeline = data.arr("timeline")
        return OrderDetail(
            number = order.optString("order_number"),
            status = order.optString("status_label"),
            paymentStatus = order.optString("payment_status"),
            total = order.optDouble("total"),
            tracking = order.optString("tracking_number"),
            carrier = order.optString("tracking_carrier"),
            lines = List(items.length()) {
                val row = items.getJSONObject(it)
                "${row.optString("name")} · ${row.optString("size")} × ${row.optInt("quantity")}"
            },
            timeline = List(timeline.length()) {
                val row = timeline.getJSONObject(it)
                TimelineEvent(row.optString("status_label"), row.optString("note"), row.optString("at"))
            }
        )
    }

    fun track(number: String, email: String): OrderDetail {
        val data = get("orders.php?action=track&number=${URLEncoder.encode(number, "UTF-8")}&email=${URLEncoder.encode(email, "UTF-8")}").obj("data")
        val timeline = data.arr("timeline")
        return OrderDetail(
            number = data.optString("order_number"),
            status = data.optString("status_label"),
            paymentStatus = data.optString("payment_status"),
            total = 0.0,
            tracking = data.optString("tracking_number"),
            carrier = data.optString("tracking_carrier"),
            lines = emptyList(),
            timeline = List(timeline.length()) {
                val row = timeline.getJSONObject(it)
                TimelineEvent(row.optString("status_label"), row.optString("note"), row.optString("at"))
            }
        )
    }

    fun checkout(fields: Map<String, String>): String {
        val data = post("orders.php?action=checkout", JSONObject(fields)).obj("data")
        return data.optString("order_number")
    }

    fun notifications(): List<Note> {
        val items = get("notifications.php").obj("data").arr("items")
        return List(items.length()) {
            val row = items.getJSONObject(it)
            Note(row.getInt("id"), row.optString("title"), row.optString("message"), row.optBoolean("is_read"))
        }
    }

    fun readAllNotes() {
        post("notifications.php?action=read_all", JSONObject())
    }

    fun journal(): List<JournalCard> {
        val items = get("journal.php").obj("data").arr("items")
        return List(items.length()) {
            val row = items.getJSONObject(it)
            JournalCard(row.optString("title"), row.optString("slug"), row.optString("excerpt"))
        }
    }

    fun article(slug: String): JournalPost {
        val data = get("journal.php?slug=${URLEncoder.encode(slug, "UTF-8")}").obj("data")
        return JournalPost(data.optString("title"), data.optString("body"), data.optString("author_name"))
    }

    fun contact(name: String, email: String, subject: String, message: String) {
        post("contact.php", json("name" to name, "email" to email, "subject" to subject, "message" to message))
    }

    fun chat(message: String): String = post("chat.php", json("message" to message)).obj("data").optString("reply")

    private fun get(path: String) = send("GET", path, null)
    private fun post(path: String, body: JSONObject) = send("POST", path, body)

    private fun send(method: String, path: String, body: JSONObject?): JSONObject {
        val builder = Request.Builder().url(baseUrl + path)
        session.token()?.let { builder.header("Authorization", "Bearer $it") }
        val cookie = session.cookieHeader()
        if (cookie.isNotBlank()) builder.header("Cookie", cookie)
        val request = when (method) {
            "GET" -> builder.get()
            "DELETE" -> builder.delete((body ?: JSONObject()).toString().toRequestBody(jsonType))
            "PUT" -> builder.put((body ?: JSONObject()).toString().toRequestBody(jsonType))
            else -> builder.post((body ?: JSONObject()).toString().toRequestBody(jsonType))
        }.build()
        client.newCall(request).execute().use { response ->
            capture(response)
            val raw = response.body?.string().orEmpty()
            if (raw.isBlank()) throw IllegalStateException("Empty response (${response.code})")
            val json = JSONObject(raw)
            if (!json.optBoolean("success", false)) {
                throw IllegalStateException(failureMessage(json))
            }
            return json
        }
    }

    private fun capture(response: okhttp3.Response) {
        val fresh = response.headers("Set-Cookie")
        if (fresh.isEmpty()) return
        val jar = session.cookieHeader().split("; ").filter { it.contains("=") }
            .associate { it.substringBefore("=") to it }.toMutableMap()
        fresh.forEach { header ->
            val pair = header.substringBefore(";")
            jar[pair.substringBefore("=")] = pair
        }
        session.saveCookie(jar.values.joinToString("; "))
    }

    private fun failureMessage(json: JSONObject): String {
        val errors = json.optJSONObject("data")?.optJSONObject("errors")
        val detail = if (errors == null) "" else {
            val names = errors.names()
            if (names == null) "" else (0 until names.length()).joinToString(" ") { errors.optString(names.getString(it)) }
        }
        return listOf(json.optString("message"), detail).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Request failed" }
    }

    private fun json(vararg pairs: Pair<String, Any?>): JSONObject {
        val obj = JSONObject()
        pairs.forEach { obj.put(it.first, it.second) }
        return obj
    }

    private fun JSONObject.obj(key: String) = getJSONObject(key)
    private fun JSONObject.arr(key: String) = optJSONArray(key) ?: JSONArray()

    private fun JSONArray.products(): List<ProductCard> = List(length()) {
        val row = getJSONObject(it)
        ProductCard(row.getInt("id"), row.optString("name"), row.optString("brand"), row.optDouble("price"), row.optString("image"))
    }

    private fun JSONArray.strings(key: String) = List(length()) { getJSONObject(it).optString(key) }.filter { it.isNotBlank() }

    private fun JSONArray.named() = List(length()) {
        val row = getJSONObject(it)
        NamedSlug(row.optString("name"), row.optString("slug"))
    }

    private fun accountFrom(user: JSONObject, profile: JSONObject) = Account(
        firstName = user.optString("first_name"),
        lastName = user.optString("last_name"),
        email = user.optString("email"),
        phone = user.optString("phone"),
        line1 = profile.optString("address_line1"),
        city = profile.optString("city"),
        province = profile.optString("province"),
        postal = profile.optString("postal_code"),
        role = user.optString("role", "customer")
    )

    companion object {
        fun imageUrl(raw: String, apiBase: String): String {
            val host = apiBase.substringBefore("/art-on-denim/")
            return raw.replace("http://localhost", host).replace("http://127.0.0.1", host)
        }
    }
}
