package za.co.artondenim

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import za.co.artondenim.ui.AodRoot
import za.co.artondenim.ui.HandoffBus
import za.co.artondenim.ui.theme.AodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handle(intent)
        enableEdgeToEdge()
        setContent {
            AodTheme {
                AodRoot()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
    }

    private fun handle(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.scheme == "aod" && data.host == "auth") {
            HandoffBus.code = data.getQueryParameter("code")
        }
    }
}
