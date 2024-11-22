package com.su.communityconnect.ui.theme
import com.su.communityconnect.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),   // Small components like text fields
    medium = RoundedCornerShape(8.dp), // Medium components like cards
    large = RoundedCornerShape(16.dp)  // Large components like dialog corners
)
// Define Inter FontFamily
val InterFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_semi_bold, FontWeight.SemiBold),
    Font(R.font.inter_black, FontWeight.Black),
    Font(R.font.inter_light, FontWeight.Light),
)

private val DarkColorPalette = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    error = DarkError,
    onPrimary = DarkOnPrimary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSecondary = DarkOnBackgroundSecondary,
    onTertiary = DarkLinkText
)

private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = InterFont),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = InterFont),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = InterFont),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = InterFont),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = InterFont),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = InterFont),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = InterFont),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = InterFont),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = InterFont),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = InterFont),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = InterFont),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = InterFont),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = InterFont),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = InterFont),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = InterFont)
)


@Composable
fun CommunityConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}