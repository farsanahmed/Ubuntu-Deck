package com.droidlab.droidlab.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

// Material Theme color definitions
@Immutable
data class Val(
    val color: Color,
    @Stable var isLight: Boolean = true
)

// Primary colors
val Purple80 = Color(0xFF6750A4)
val PurpleGrey80 = Color(0xFF5D526D)
val Pink80 = Color(0xFFEF578A)
val Purple40 = Color(0xFFD0BCFF)
val PurpleGrey40 = Color(0xFFCCC2DC)
val Pink40 = Color(0xFFFFB4C4)

// You can add your own custom colors here
val DroidLabBlue = Color(0xFF0D47A1)
val DroidLabGreen = Color(0xFF1B5E20)
val DroidLabOrange = Color(0xFFEF6C00)
