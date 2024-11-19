package com.su.communityconnect.ui.theme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.su.communityconnect.R

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),   // Small components like text fields
    medium = RoundedCornerShape(8.dp), // Medium components like cards
    large = RoundedCornerShape(16.dp)  // Large components like dialog corners
)
// Define Inter FontFamily
val InterFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_bold, FontWeight.Bold)
)

// Define Dark Colors
private val DarkColorPalette = darkColors(
    primary = Color(0xFF6964D3), // Button and primary color
    primaryVariant = Color(0xFF5A55C9),
    secondary = Color.White,    // Text color
    background = Color.Black    // App background
)

// Typography
val Typography = androidx.compose.material.Typography(
    defaultFontFamily = InterFont,
    body1 = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)


@Composable
fun CommunityConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}