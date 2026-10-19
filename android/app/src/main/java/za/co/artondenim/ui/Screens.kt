package za.co.artondenim.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import za.co.artondenim.BuildConfig
import za.co.artondenim.CheckoutRules
import za.co.artondenim.data.Account
import za.co.artondenim.data.AodApi
import za.co.artondenim.data.ProductCard
import za.co.artondenim.formatZar
import za.co.artondenim.ui.theme.Brass
import za.co.artondenim.ui.theme.Chalk
import za.co.artondenim.ui.theme.Ink
import za.co.artondenim.ui.theme.Muted
import za.co.artondenim.ui.theme.OutfitFamily
import za.co.artondenim.ui.theme.SyneFamily

@Composable
fun SiteHeader(showAdmin: Boolean, onNav: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Chalk)) {
        Text(
            "Complimentary shipping on orders over R1,500.00",
            color = Chalk,
            fontFamily = OutfitFamily,
            fontSize = 11.sp,
            letterSpacing = 0.6.sp,
            modifier = Modifier.fillMaxWidth().background(Ink).padding(vertical = 8.dp, horizontal = 16.dp)
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.clickable { onNav("home") }) {
                Text("AOD", color = Ink, fontFamily = SyneFamily, fontWeight = FontWeight.Bold)
                Text("Art on Denim", color = Muted, fontFamily = OutfitFamily, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    "shop/all" to "Search",
                    "notes" to "Notes",
                    "account" to "Account",
                    "saved" to "Saved",
                    "cart" to "Bag"
                ).forEach { (route, label) ->
                    Text(label, color = Ink, fontFamily = OutfitFamily, fontSize = 12.sp, modifier = Modifier.clickable { onNav(route) })
                }
                if (showAdmin) {
                    Text("Studio", color = Brass, fontFamily = OutfitFamily, fontSize = 12.sp, modifier = Modifier.clickable { onNav("admin") })
                }
            }
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            listOf(
                "shop/all" to "Shop",
                "shop/new" to "New",
                "brands" to "Brands",
                "shop/sale" to "Sale",
                "journal" to "Journal",
                "about" to "About",
                "contact" to "Contact"
            ).forEach { (route, label) ->
                Text(label, color = Ink, fontFamily = OutfitFamily, fontSize = 14.sp, modifier = Modifier.clickable { onNav(route) })
            }
        }
    }
}

@Composable
fun HomeScreen(model: ShopModel, onOpen: (Int) -> Unit, onShop: (String) -> Unit, onJournal: () -> Unit, onArticle: (String) -> Unit) {
    LaunchedEffect(Unit) { model.loadHome() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Box(Modifier.fillMaxWidth().height(420.dp).background(Ink)) {
                AsyncImage(
                    model = BuildConfig.API_BASE.substringBefore("/api/") + "/assets/images/hero-denim.jpg",
                    contentDescription = "Art on Denim",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(Modifier.fillMaxSize().background(Ink.copy(alpha = 0.45f)).padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("01 — Johannesburg", color = Chalk.copy(alpha = 0.72f), fontFamily = OutfitFamily)
                    Text("Art on Denim", color = Brass, fontFamily = SyneFamily)
                    Text("Denim craft. Designer edge.", color = Chalk, fontFamily = SyneFamily, fontSize = 32.sp)
                    Text("Curated house pieces and international labels — built for presence.", color = Chalk, fontFamily = OutfitFamily)
                    Text("House denim · Designer edit · Priced in rand", color = Chalk.copy(alpha = 0.72f), fontFamily = OutfitFamily)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallButton("Shop collection", Modifier.weight(1f), "primary") { onShop("") }
                        SmallButton("New arrivals", Modifier.weight(1f), "ghost") { onShop("new") }
                    }
                }
            }
        }
        item { SectionTitle("02 — The floor", "New arrivals") { onShop("new") } }
        items(model.homeNew.take(4)) { ProductRow(it, onOpen) }
        item {
            Text("House label", color = Brass)
            Text("New AOD Collection", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            SmallButton("Explore the house", Modifier.fillMaxWidth()) { onShop("aod") }
        }
        item {
            Text(
                model.brands.joinToString("     ").ifBlank { "Art on Denim" },
                color = Ink,
                fontFamily = SyneFamily,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().basicMarquee()
            )
        }
        item { SectionTitle("Limited", "On sale") { onShop("sale") } }
        items(model.homeSale.take(4)) { ProductRow(it, onOpen) }
        item { SectionTitle("Journal", "From the studio") { onJournal() } }
        items(model.posts) { post ->
            Column(Modifier.clickable { onArticle(post.slug) }) {
                Text(post.title, color = Ink)
                Text(post.excerpt, color = Muted)
            }
        }
        item { Status(model) }
    }
}

@Composable
fun ShopScreen(model: ShopModel, preset: String, onOpen: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf("newest") }
    var category by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf(if (preset.startsWith("brand-")) preset.removePrefix("brand-") else "") }
    var gender by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf(0f) }
    var maxPrice by remember { mutableStateOf(20000f) }
    var onlyNew by remember { mutableStateOf(preset == "new") }
    var onlySale by remember { mutableStateOf(preset == "sale") }
    fun filters() = buildMap {
        if (query.isNotBlank()) put("q", query)
        put("sort", sort)
        if (category.isNotBlank()) put("category", category)
        if (brand.isNotBlank()) put("brand", brand)
        if (gender.isNotBlank()) put("gender", gender)
        if (size.isNotBlank()) put("size", size)
        if (minPrice > 0f) put("min_price", minPrice.toInt().toString())
        if (maxPrice < 20000f) put("max_price", maxPrice.toInt().toString())
        if (onlyNew) put("new", "1")
        if (onlySale) put("sale", "1")
        if (preset == "aod") put("collection", "aod")
    }
    LaunchedEffect(preset) {
        model.loadMeta()
        model.loadShop(filters())
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Column(Modifier.fillMaxWidth().background(Ink).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Catalogue", color = Brass, fontFamily = OutfitFamily, fontSize = 12.sp)
                Field("Search", query) { query = it }
                FilterRow("Sort", listOf("newest" to "Newest", "featured" to "Featured", "price_asc" to "Price: Low to High", "price_desc" to "Price: High to Low", "name_asc" to "Name A–Z"), sort) { sort = it }
                FilterRow("Category", listOf("" to "All categories") + model.catalogue.categories.map { it.slug to it.name }, category) { category = it }
                FilterRow("Brand", listOf("" to "All brands") + model.catalogue.brands.map { it.slug to it.name }, brand) { brand = it }
                FilterRow("Gender", listOf("" to "All", "unisex" to "Unisex", "men" to "Men", "women" to "Women"), gender) { gender = it }
                Text("Min R${minPrice.toInt()}", color = Chalk, fontFamily = OutfitFamily)
                Slider(value = minPrice, onValueChange = { minPrice = it }, valueRange = 0f..20000f, steps = 19)
                Text("Max R${maxPrice.toInt()}", color = Chalk, fontFamily = OutfitFamily)
                Slider(value = maxPrice, onValueChange = { maxPrice = it }, valueRange = 0f..20000f, steps = 19)
                FilterRow("Size", listOf("" to "Any size") + model.catalogue.sizes.map { it to it }, size) { size = it }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallButton(if (onlyNew) "New arrivals on" else "New arrivals", Modifier.weight(1f), if (onlyNew) "brass" else "ghost") { onlyNew = !onlyNew }
                    SmallButton(if (onlySale) "On sale on" else "On sale", Modifier.weight(1f), if (onlySale) "brass" else "ghost") { onlySale = !onlySale }
                }
                SmallButton("Apply filters", Modifier.fillMaxWidth(), "brass") { model.loadShop(filters()) }
                SmallButton("Clear all", Modifier.fillMaxWidth(), "ghost") {
                    query = ""; sort = "newest"; category = ""; brand = ""; gender = ""; size = ""
                    minPrice = 0f; maxPrice = 20000f; onlyNew = false; onlySale = false
                    model.loadShop(emptyMap())
                }
            }
        }
        item { Status(model) }
        items(model.products, key = { it.id }) { ProductRow(it, onOpen) }
    }
}

@Composable
private fun FilterRow(label: String, options: List<Pair<String, String>>, selected: String, onPick: (String) -> Unit) {
    Text(label, color = Chalk.copy(alpha = 0.7f), fontFamily = OutfitFamily, fontSize = 12.sp)
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, name) ->
            Text(
                name,
                color = if (selected == value) Ink else Chalk,
                fontFamily = OutfitFamily,
                fontSize = 12.sp,
                modifier = Modifier.background(if (selected == value) Chalk else Ink).clickable { onPick(value) }.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun BrandsScreen(model: ShopModel, onBrand: (String) -> Unit) {
    LaunchedEffect(Unit) { model.loadMeta() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Brands", color = Ink, fontFamily = SyneFamily, fontSize = 32.sp) }
        items(model.catalogue.brands, key = { it.slug }) { brand ->
            Text(brand.name, color = Ink, fontFamily = SyneFamily, fontSize = 20.sp, modifier = Modifier.fillMaxWidth().clickable { onBrand(brand.slug) }.padding(vertical = 8.dp))
        }
        item { Status(model) }
    }
}

@Composable
fun AdminScreen(model: ShopModel, onOrder: (String) -> Unit) {
    LaunchedEffect(Unit) { model.loadAdmin() }
    val desk = model.admin
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Studio", color = Ink, fontFamily = SyneFamily, fontSize = 32.sp) }
        item { Text("The same desk as the website admin.", color = Muted, fontFamily = OutfitFamily) }
        if (desk != null) {
            item { Text("Total sales ${formatZar(desk.sales)}", color = Ink, fontFamily = SyneFamily) }
            item { Text("Orders today ${desk.ordersToday} · Total orders ${desk.ordersTotal}", color = Ink, fontFamily = OutfitFamily) }
            item { Text("Customers ${desk.customers} · Active products ${desk.products}", color = Ink, fontFamily = OutfitFamily) }
            item { Text("Low stock SKUs ${desk.lowStock} · New messages ${desk.messages}", color = Ink, fontFamily = OutfitFamily) }
            item { Text("Recent orders", color = Brass, fontFamily = OutfitFamily) }
            items(desk.orders) { line ->
                val number = line.substringBefore(" ·")
                Text(line, color = Ink, fontFamily = OutfitFamily, modifier = Modifier.clickable { onOrder(number) })
            }
            item { Text("Low stock", color = Brass, fontFamily = OutfitFamily) }
            items(desk.stock) { Text(it, color = Ink, fontFamily = OutfitFamily) }
        }
        item {
            SmallButton("Customers", Modifier.fillMaxWidth(), "ghost-dark") {
                model.loadAdminLines("customers")
            }
        }
        items(model.adminPeople) { Text(it, color = Ink, fontFamily = OutfitFamily) }
        item {
            SmallButton("Messages", Modifier.fillMaxWidth(), "ghost-dark") {
                model.loadAdminLines("messages")
            }
        }
        items(model.adminMessages) { Text(it, color = Ink, fontFamily = OutfitFamily) }
        item { Status(model) }
    }
}

@Composable
fun ProductScreen(model: ShopModel, id: Int, onOpen: (Int) -> Unit) {
    var size by remember { mutableStateOf("") }
    LaunchedEffect(id) { model.loadProduct(id) }
    val product = model.detail
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (product == null) {
            item { Status(model) }
        } else {
            item {
                AsyncImage(AodApi.imageUrl(product.image, BuildConfig.API_BASE), product.name, Modifier.fillMaxWidth().height(360.dp).background(Ink), contentScale = ContentScale.Crop)
                Text(product.brand.uppercase(), color = Brass)
                Text(product.name, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Text(formatZar(product.price))
                Text(product.description, color = Muted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.sizes.forEach { option ->
                        Text(
                            option.size,
                            color = if (size == option.size) Chalk else Ink,
                            modifier = Modifier.background(if (size == option.size) Ink else Chalk, RoundedCornerShape(8.dp))
                                .clickable(enabled = option.inStock) { size = option.size }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
                Status(model)
                SmallButton(
                    if (size.isBlank()) "Choose a size" else "Add to bag",
                    Modifier.fillMaxWidth(),
                    enabled = size.isNotBlank()
                ) { model.add(id, size) }
                SmallButton("Save", Modifier.fillMaxWidth(), "ghost-dark") { model.wish(id) }
                Text("Also in this edit", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            }
            items(product.related) { ProductRow(it, onOpen) }
        }
    }
}

@Composable
fun CartScreen(model: ShopModel, onCheckout: () -> Unit, onShop: () -> Unit) {
    var code by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { model.loadCart() }
    val cart = model.cart
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Bag", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge) }
        if (cart == null || cart.items.isEmpty()) {
            item {
                Text("Your bag is empty.", color = Muted)
                SmallButton("Continue shopping", Modifier.fillMaxWidth(), "ghost-dark") { onShop() }
            }
        } else {
            items(cart.items) { line ->
                Text("${line.name} · ${line.size} × ${line.quantity}")
                Text(formatZar(line.lineTotal), color = Muted)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Less", color = Brass, modifier = Modifier.clickable { model.changeQty(line.id, line.quantity - 1) })
                    Text("More", color = Brass, modifier = Modifier.clickable { model.changeQty(line.id, line.quantity + 1) })
                }
            }
            item {
                Text("Subtotal ${formatZar(cart.subtotal)}")
                Text("Coupon ${cart.coupon ?: "none"} ${formatZar(cart.discount)}")
                Text("Shipping ${formatZar(cart.shipping)}")
                Text("Total ${formatZar(cart.total)}")
                Field("Coupon code", code) { code = it }
                SmallButton("Apply", Modifier.fillMaxWidth(), "brass") { model.coupon(code) }
                if (cart.coupon != null) SmallButton("Remove coupon", Modifier.fillMaxWidth(), "ghost-dark") { model.clearCoupon() }
                SmallButton("Checkout", Modifier.fillMaxWidth()) { onCheckout() }
                SmallButton("Continue shopping", Modifier.fillMaxWidth(), "ghost-dark") { onShop() }
            }
        }
        item { Status(model) }
    }
}

@Composable
fun CheckoutScreen(model: ShopModel, onPlaced: (String) -> Unit) {
    val account = model.account
    var name by remember { mutableStateOf(listOfNotNull(account?.firstName, account?.lastName).joinToString(" ")) }
    var email by remember { mutableStateOf(account?.email.orEmpty()) }
    var phone by remember { mutableStateOf(account?.phone.orEmpty()) }
    var line by remember { mutableStateOf(account?.line1.orEmpty()) }
    var city by remember { mutableStateOf(account?.city.orEmpty()) }
    var province by remember { mutableStateOf(account?.province.orEmpty()) }
    var postal by remember { mutableStateOf(account?.postal.orEmpty()) }
    var method by remember { mutableStateOf("payfast") }
    var localError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { if (model.signedIn()) model.loadAccount() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Checkout", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge) }
        item { Field("Full name", name) { name = it } }
        item { Field("Email", email) { email = it } }
        item { Field("Phone", phone) { phone = it } }
        item { Field("Address", line) { line = it } }
        item { Field("City", city) { city = it } }
        item { Field("Postal code", postal) { postal = it } }
        item {
            Text("Province", color = Muted)
            CheckoutRules.provinces.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { option ->
                        Text(option, color = if (province == option) Brass else Ink, modifier = Modifier.clickable { province = option }.padding(4.dp))
                    }
                }
            }
        }
        item {
            Text("Payment", color = Muted)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("payfast" to "Payfast", "eft" to "EFT", "card" to "Card").forEach { (value, label) ->
                    Text(label, color = if (method == value) Brass else Ink, modifier = Modifier.clickable { method = value })
                }
            }
        }
        item {
            localError?.let { Text(it, color = Brass) }
            Status(model)
            SmallButton("Place order", Modifier.fillMaxWidth()) {
                val fields = mapOf(
                    "full_name" to name,
                    "email" to email,
                    "phone" to phone,
                    "address_line1" to line,
                    "city" to city,
                    "province" to province,
                    "postal_code" to postal,
                    "payment_method" to method
                )
                val problems = CheckoutRules.errors(fields)
                if (problems.isNotEmpty()) localError = problems.values.joinToString(" ")
                else model.checkout(fields, onPlaced)
            }
        }
    }
}

@Composable
fun OrdersScreen(model: ShopModel, onOpen: (String) -> Unit) {
    LaunchedEffect(Unit) { model.loadOrders() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Orders", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge) }
        items(model.orders) { order ->
            Column(Modifier.fillMaxWidth().clickable { onOpen(order.number) }) {
                Text(order.number)
                Text("${order.status} · ${formatZar(order.total)}", color = Muted)
            }
        }
        item { Status(model) }
    }
}

@Composable
fun OrderScreen(model: ShopModel, number: String) {
    LaunchedEffect(number) { if (number.isNotBlank()) model.loadOrder(number) }
    val order = model.order
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (order == null) Status(model) else {
            Text(order.number, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Text(order.status)
            Text("Payment ${order.paymentStatus}", color = Muted)
            if (order.total > 0) Text(formatZar(order.total))
            if (order.tracking.isNotBlank()) Text("${order.carrier} ${order.tracking}")
            order.lines.forEach { Text(it) }
            order.timeline.forEach { Text("${it.label} · ${it.note}", color = Muted) }
        }
    }
}

@Composable
fun TrackScreen(model: ShopModel) {
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Track an order", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Field("Order number", number) { number = it }
        Field("Email", email) { email = it }
        SmallButton("Track order", Modifier.fillMaxWidth()) { model.track(number, email) }
        Status(model)
        model.order?.let { order ->
            Text(order.status)
            order.timeline.forEach { Text("${it.label} · ${it.at}", color = Muted) }
        }
    }
}

@Composable
fun SavedScreen(model: ShopModel, onOpen: (Int) -> Unit) {
    LaunchedEffect(Unit) { model.loadSaved() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Saved", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge) }
        if (model.saved.isEmpty()) item { Text("Nothing saved yet.", color = Muted) }
        items(model.saved, key = { it.id }) { product ->
            ProductRow(product, onOpen)
            Text("Remove", color = Brass, modifier = Modifier.clickable { model.unwish(product.id) })
        }
        item { Status(model) }
    }
}

@Composable
fun NotesScreen(model: ShopModel) {
    LaunchedEffect(Unit) { model.loadNotes() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Notifications", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
            SmallButton("Mark all read", Modifier.fillMaxWidth(), "ghost-dark") { model.readNotes() }
        }
        items(model.notes) { note ->
            Text(note.title, color = if (note.read) Muted else Ink)
            Text(note.message, color = Muted)
        }
        item { Status(model) }
    }
}

@Composable
fun AccountScreen(model: ShopModel, onOrders: () -> Unit, onNotes: () -> Unit, onTrack: () -> Unit, onJournal: () -> Unit, onAbout: () -> Unit, onContact: () -> Unit, onChat: () -> Unit, onAdmin: () -> Unit, onSignedOut: () -> Unit) {
    LaunchedEffect(Unit) { if (model.signedIn()) model.loadAccount() }
    val current = model.account
    var first by remember(current) { mutableStateOf(current?.firstName.orEmpty()) }
    var last by remember(current) { mutableStateOf(current?.lastName.orEmpty()) }
    var phone by remember(current) { mutableStateOf(current?.phone.orEmpty()) }
    var line by remember(current) { mutableStateOf(current?.line1.orEmpty()) }
    var city by remember(current) { mutableStateOf(current?.city.orEmpty()) }
    var province by remember(current) { mutableStateOf(current?.province.orEmpty()) }
    var postal by remember(current) { mutableStateOf(current?.postal.orEmpty()) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Account", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge) }
        item { Text(current?.email.orEmpty(), color = Muted) }
        item { Field("First name", first) { first = it } }
        item { Field("Last name", last) { last = it } }
        item { Field("Phone", phone) { phone = it } }
        item { Field("Address", line) { line = it } }
        item { Field("City", city) { city = it } }
        item { Field("Province", province) { province = it } }
        item { Field("Postal code", postal) { postal = it } }
        item {
            SmallButton("Save profile", Modifier.fillMaxWidth()) {
                model.saveAccount(Account(first, last, current?.email.orEmpty(), phone, line, city, province, postal))
            }
        }
        item {
            if (current?.role == "admin") Link("Studio", onAdmin)
            Link("Orders", onOrders); Link("Notifications", onNotes); Link("Track", onTrack); Link("Journal", onJournal); Link("About", onAbout); Link("Contact", onContact); Link("Ask the studio", onChat)
        }
        item { Status(model); SmallButton("Sign out", Modifier.fillMaxWidth(), "ghost-dark") { model.signOut(onSignedOut) } }
    }
}

@Composable
fun JournalScreen(model: ShopModel, onOpen: (String) -> Unit) {
    LaunchedEffect(Unit) { model.loadJournal() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Journal", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge) }
        items(model.posts) { post ->
            Column(Modifier.clickable { onOpen(post.slug) }) {
                Text(post.title)
                Text(post.excerpt, color = Muted)
            }
        }
        item { Status(model) }
    }
}

@Composable
fun ArticleScreen(model: ShopModel, slug: String) {
    LaunchedEffect(slug) { model.loadArticle(slug) }
    val post = model.article
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (post == null) Status(model) else {
            Text(post.title, style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
            Text(post.author, color = Brass)
            Text(post.body)
        }
    }
}

@Composable
fun AboutScreen(onShop: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("AOD", color = Brass)
        Text("About Art on Denim", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Text("Art on Denim is a South African premium streetwear and designer fashion store, built around denim craft, curated international labels, and a clean modern wardrobe.")
        Text("From house-cut selvedge to selected Rick Owens and contemporary luxury essentials, every piece is chosen for presence, construction, and longevity.")
        SmallButton("Shop the collection", Modifier.fillMaxWidth()) { onShop() }
    }
}

@Composable
fun ContactScreen(model: ShopModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Contact", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Field("Name", name) { name = it }
        Field("Email", email) { email = it }
        Field("Subject", subject) { subject = it }
        Field("Message", message) { message = it }
        SmallButton("Send message", Modifier.fillMaxWidth()) { model.sendContact(name, email, subject, message) }
        Status(model)
    }
}

@Composable
fun AuthScreen(model: ShopModel, register: Boolean, onSubmit: (String, String, String, String) -> Unit, onSwitch: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit) { model.loadMeta() }
    Column(Modifier.fillMaxSize().background(Chalk).padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Account", color = Brass)
        Text(if (register) "Create an account" else "Sign in", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Text(if (register) "New to Art on Denim?" else "Access your orders, wishlist, and saved details.", color = Muted)
        if (register) {
            Field("First name", first) { first = it }
            Field("Last name", last) { last = it }
        }
        Field("Email address", email) { email = it }
        Field("Password", password, password = true) { password = it }
        Status(model)
        SmallButton(if (register) "Create account" else "Sign in", Modifier.fillMaxWidth()) { onSubmit(email, password, first, last) }
        if (!register && model.googleReady) {
            SmallButton("Continue with Google", Modifier.fillMaxWidth(), "brass") {
                val root = BuildConfig.API_BASE.substringBefore("/api/")
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$root/pages/google-start.php?handoff=1")))
            }
        } else if (!register) {
            Text("Google sign-in is ready once config/google.local.php has a client id and secret.", color = Muted)
        }
        Text(if (register) "Already have an account" else "Create an account", color = Brass, modifier = Modifier.clickable(onClick = onSwitch).padding(top = 12.dp))
    }
}

@Composable
fun ChatScreen(model: ShopModel) {
    var message by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ask the studio", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        LazyColumn(Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(model.chat) { Text(it) }
        }
        Field("Message", message) { message = it }
        SmallButton("Send", Modifier.fillMaxWidth(), "brass") {
            if (message.isNotBlank()) {
                val text = message
                message = ""
                model.ask(text)
            }
        }
        Status(model)
    }
}

@Composable
fun ProductRow(product: ProductCard, onOpen: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().clickable { onOpen(product.id) }) {
        AsyncImage(
            AodApi.imageUrl(product.image, BuildConfig.API_BASE),
            product.name,
            Modifier.fillMaxWidth().aspectRatio(0.8f).background(Ink),
            contentScale = ContentScale.Crop
        )
        Text(product.brand.uppercase(), color = Brass, fontFamily = OutfitFamily, letterSpacing = 1.2.sp, modifier = Modifier.padding(top = 10.dp))
        Text(product.name, fontFamily = SyneFamily, fontWeight = FontWeight.Medium)
        Text(formatZar(product.price), fontFamily = OutfitFamily)
    }
}

@Composable
fun SectionTitle(eyebrow: String, title: String, onMore: () -> Unit) {
    Text(eyebrow, color = Brass)
    Text(title, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
    Text("View all", color = Brass, modifier = Modifier.clickable(onClick = onMore))
}

@Composable
fun Field(label: String, value: String, password: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = label != "Message",
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Ink,
            unfocusedTextColor = Ink,
            focusedBorderColor = Brass,
            unfocusedBorderColor = Muted,
            focusedLabelColor = Brass,
            unfocusedLabelColor = Muted,
            cursorColor = Brass
        )
    )
}

@Composable
fun SmallButton(
    label: String,
    modifier: Modifier = Modifier,
    kind: String = "inverse",
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val background = when (kind) {
        "primary" -> Color(0xFFF2EFE9)
        "brass" -> Brass
        "ghost", "ghost-dark" -> Color.Transparent
        else -> Ink
    }
    val foreground = when (kind) {
        "ghost" -> Chalk
        "inverse" -> Chalk
        else -> Ink
    }
    val border = when (kind) {
        "ghost" -> BorderStroke(1.dp, Chalk.copy(alpha = 0.45f))
        "ghost-dark" -> BorderStroke(1.dp, Ink.copy(alpha = 0.35f))
        else -> null
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(9.dp),
        border = border,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = foreground,
            disabledContainerColor = Ink.copy(alpha = 0.18f),
            disabledContentColor = Muted
        )
    ) {
        Text(
            text = label.uppercase(),
            color = if (enabled) foreground else Muted,
            fontFamily = OutfitFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Link(label: String, onClick: () -> Unit) {
    Text(label, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp))
}

@Composable
fun Status(model: ShopModel) {
    if (model.busy) CircularProgressIndicator(color = Brass, modifier = Modifier.padding(8.dp))
    model.error?.let { Text(it, color = Brass) }
    model.notice?.let { Text(it, color = Ink) }
}
