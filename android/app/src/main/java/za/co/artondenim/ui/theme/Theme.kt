package za.co.artondenim.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import za.co.artondenim.R

val SyneFamily = FontFamily(
    Font(R.font.syne_medium, FontWeight.Normal),
    Font(R.font.syne_medium, FontWeight.Medium),
    Font(R.font.syne_bold, FontWeight.SemiBold),
    Font(R.font.syne_bold, FontWeight.Bold)
)

val OutfitFamily = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_regular, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_semibold, FontWeight.Bold)
)

val Ink = Color(0xFF0C1016)
val Denim = Color(0xFF121820)
val Chalk = Color(0xFFF7F4EF)
val Brass = Color(0xFFB8956C)
val Muted = Color(0xFF5C564C)

private val Colors = lightColorScheme(
    primary = Brass,
    onPrimary = Ink,
    background = Chalk,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE7E1D6),
    onSurfaceVariant = Muted
)

private val Type = Typography(
    headlineLarge = TextStyle(fontFamily = SyneFamily, fontWeight = FontWeight.Medium, fontSize = 36.sp, letterSpacing = (-0.8).sp),
    titleLarge = TextStyle(fontFamily = SyneFamily, fontWeight = FontWeight.Medium, fontSize = 22.sp),
    bodyLarge = TextStyle(fontFamily = OutfitFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = OutfitFamily, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = OutfitFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 0.8.sp)
)

@Composable
fun AodTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = Type, content = content)
}
