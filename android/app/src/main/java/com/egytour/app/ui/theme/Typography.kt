package com.egytour.app.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.egytour.app.R

val PlayfairDisplay = FontFamily(
    Font(R.font.playfairdisplay_regular, FontWeight.Normal),
    Font(R.font.playfairdisplay_medium, FontWeight.Medium),
    Font(R.font.playfairdisplay_semibold, FontWeight.SemiBold),
    Font(R.font.playfairdisplay_bold, FontWeight.Bold),
    Font(R.font.playfairdisplay_extrabold, FontWeight.ExtraBold),
    Font(R.font.playfairdisplay_black, FontWeight.Black),
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp
    ),
    h2 = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    h3 = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    h4 = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    h5 = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    h6 = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    button = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 1.sp
    ),
    caption = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp
    )
)
