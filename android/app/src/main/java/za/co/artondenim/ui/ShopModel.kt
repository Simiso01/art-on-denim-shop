package za.co.artondenim.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.artondenim.data.Account
import za.co.artondenim.data.AodApi
import za.co.artondenim.data.CartSnapshot
import za.co.artondenim.data.AdminDash
import za.co.artondenim.data.CatalogueMeta
import za.co.artondenim.data.JournalCard
import za.co.artondenim.data.JournalPost
import za.co.artondenim.data.Note
import za.co.artondenim.data.OrderDetail
import za.co.artondenim.data.OrderSummary
import za.co.artondenim.data.ProductCard
import za.co.artondenim.data.ProductDetail
import za.co.artondenim.data.SessionStore

class ShopModel(private val api: AodApi, private val session: SessionStore) : ViewModel() {
    var busy by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var notice by mutableStateOf<String?>(null)
    var products by mutableStateOf(listOf<ProductCard>())
    var homeNew by mutableStateOf(listOf<ProductCard>())
    var homeSale by mutableStateOf(listOf<ProductCard>())
    var brands by mutableStateOf(listOf<String>())
    var catalogue by mutableStateOf(CatalogueMeta(emptyList(), emptyList(), emptyList(), false))
    var admin by mutableStateOf<AdminDash?>(null)
    var adminPeople by mutableStateOf(listOf<String>())
    var adminMessages by mutableStateOf(listOf<String>())
    var googleReady by mutableStateOf(false)
    var detail by mutableStateOf<ProductDetail?>(null)
    var cart by mutableStateOf<CartSnapshot?>(null)
    var saved by mutableStateOf(listOf<ProductCard>())
    var orders by mutableStateOf(listOf<OrderSummary>())
    var order by mutableStateOf<OrderDetail?>(null)
    var notes by mutableStateOf(listOf<Note>())
    var posts by mutableStateOf(listOf<JournalCard>())
    var article by mutableStateOf<JournalPost?>(null)
    var account by mutableStateOf<Account?>(null)
    var chat by mutableStateOf(listOf("Ask about shipping, a size, a coupon, or a piece by name."))
    var placedNumber by mutableStateOf<String?>(null)

    fun signedIn() = session.token() != null

    fun signIn(email: String, password: String, onDone: () -> Unit) = run("Could not sign in") {
        account = api.login(email, password)
        onDone()
    }

    fun register(first: String, last: String, email: String, password: String, onDone: () -> Unit) = run("Could not create the account") {
        account = api.register(first, last, email, password)
        onDone()
    }

    fun google(code: String, onDone: () -> Unit) = run("Google sign-in failed") {
        account = api.exchangeGoogle(code)
        onDone()
    }

    fun signOut(onDone: () -> Unit) = run("Could not sign out") {
        api.logout()
        account = null
        onDone()
    }

    fun loadMeta() = run("Could not reach the store") {
        val meta = api.meta()
        catalogue = meta
        brands = meta.brands.map { it.name }
        googleReady = meta.google
    }

    fun loadHome() = run("Could not load the edit") {
        homeNew = api.products(mapOf("new" to "1"))
        homeSale = api.products(mapOf("sale" to "1"))
        val meta = api.meta()
        catalogue = meta
        brands = meta.brands.map { it.name }
        googleReady = meta.google
        posts = api.journal().take(3)
        if (session.token() != null) {
            try {
                account = api.me()
            } catch (_: Exception) {
            }
        }
    }

    fun loadAdmin() = run("Could not open the studio desk") {
        admin = api.admin()
    }

    fun loadAdminLines(action: String) = run("Could not load the studio list") {
        val lines = api.adminLines(action)
        if (action == "customers") adminPeople = lines else adminMessages = lines
    }

    fun loadShop(query: Map<String, String>) = run("Could not load the shop") {
        products = api.products(query)
    }

    fun loadProduct(id: Int) {
        detail = null
        run("Could not load this piece") { detail = api.product(id) }
    }

    fun loadCart() = run("Could not load the bag") { cart = api.cart() }

    fun add(productId: Int, size: String) = run("Could not add this size") {
        api.addToCart(productId, size)
        notice = "Added to bag"
        cart = api.cart()
    }

    fun changeQty(itemId: Int, quantity: Int) = run("Could not update the bag") {
        if (quantity < 1) api.removeCart(itemId) else api.updateCart(itemId, quantity)
        cart = api.cart()
    }

    fun coupon(code: String) = run("Could not apply the coupon") {
        api.applyCoupon(code)
        cart = api.cart()
    }

    fun clearCoupon() = run("Could not remove the coupon") {
        api.removeCoupon()
        cart = api.cart()
    }

    fun loadSaved() = run("Could not load saved pieces") { saved = api.wishlist() }

    fun wish(productId: Int) = run("Could not save this piece") {
        api.saveWish(productId)
        notice = "Saved"
    }

    fun unwish(productId: Int) = run("Could not remove this piece") {
        api.removeWish(productId)
        saved = api.wishlist()
    }

    fun loadOrders() = run("Could not load orders") { orders = api.orders() }

    fun loadOrder(number: String) {
        order = null
        run("Could not load the order") { order = api.order(number) }
    }

    fun track(number: String, email: String) {
        order = null
        run("Could not track that order") { order = api.track(number, email) }
    }

    fun checkout(fields: Map<String, String>, onDone: (String) -> Unit) = run("Could not place the order") {
        val number = api.checkout(fields)
        placedNumber = number
        cart = api.cart()
        onDone(number)
    }

    fun loadNotes() = run("Could not load notifications") { notes = api.notifications() }

    fun readNotes() = run("Could not update notifications") {
        api.readAllNotes()
        notes = api.notifications()
    }

    fun loadJournal() = run("Could not load the journal") { posts = api.journal() }

    fun loadArticle(slug: String) {
        article = null
        run("Could not load the article") { article = api.article(slug) }
    }

    fun loadAccount() = run("Could not load the account") { account = api.me() }

    fun saveAccount(value: Account) = run("Could not save the profile") {
        api.saveProfile(value)
        account = api.me()
        notice = "Profile saved"
    }

    fun sendContact(name: String, email: String, subject: String, message: String) = run("Could not send the message") {
        api.contact(name, email, subject, message)
        notice = "Message sent. The studio will reply by email."
    }

    fun ask(message: String) = run("The studio could not answer") {
        chat = chat + message
        chat = chat + api.chat(message)
    }

    private fun run(fallback: String, block: () -> Unit) {
        viewModelScope.launch {
            busy = true
            error = null
            notice = null
            try {
                withContext(Dispatchers.IO) { block() }
            } catch (ex: Exception) {
                error = ex.message?.ifBlank { fallback } ?: fallback
            } finally {
                busy = false
            }
        }
    }

    companion object {
        fun factory(api: AodApi, session: SessionStore) = viewModelFactory {
            initializer { ShopModel(api, session) }
        }
    }
}
