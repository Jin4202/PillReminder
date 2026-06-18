package com.example.pillreminder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pillreminder.R

val AppFontFamily = FontFamily(
    Font(R.font.lato_thin,          FontWeight.Thin),
    Font(R.font.lato_thin_italic,   FontWeight.Thin,      FontStyle.Italic),

    Font(R.font.lato_regular,       FontWeight.Normal),
    Font(R.font.lato_italic,        FontWeight.Normal,    FontStyle.Italic),

    Font(R.font.lato_bold,          FontWeight.Bold),
    Font(R.font.lato_bold_italic,   FontWeight.Bold,      FontStyle.Italic),

    Font(R.font.lato_black,         FontWeight.Black),
    Font(R.font.lato_black_italic,  FontWeight.Black,     FontStyle.Italic),
)

val Typography = Typography(
    // Largest Text
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    // Title Text
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    // Subtitle Text
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    // Basic Larger Text
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Basic Text
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // Button Text
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)