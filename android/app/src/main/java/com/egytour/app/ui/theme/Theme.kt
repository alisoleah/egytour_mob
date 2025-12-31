package com.egytour.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = PrimaryDarkTheme,
    primaryVariant = PrimaryHoverDarkTheme,
    secondary = BrandOrange,
    background = BackgroundDarkTheme,
    surface = SurfaceDarkTheme,
    onPrimary = BrandWhite,
    onSecondary = BrandBlack,
    onBackground = BrandWhite,
    onSurface = BrandWhite,
)

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = PrimaryDark,
    secondary = BrandOrange,
    background = BackgroundLight,
    surface = BrandSurface,
    onPrimary = BrandWhite,
    onSecondary = BrandBlack,
    onBackground = BrandTextMain,
    onSurface = BrandTextMain,
)

@Composable
fun EgytourTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
