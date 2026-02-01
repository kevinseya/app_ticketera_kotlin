package com.jwmaila.appticketera.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MediumBlue,
    onPrimary = White,
    primaryContainer = PastelBlue,
    onPrimaryContainer = DarkBlue,
    
    secondary = LightBlue,
    onSecondary = White,
    secondaryContainer = VeryLightBlue,
    onSecondaryContainer = DarkBlue,
    
    tertiary = DarkBlue,
    onTertiary = White,
    
    background = White,
    onBackground = Black,
    
    surface = White,
    onSurface = Black,
    surfaceVariant = LightGray,
    onSurfaceVariant = Gray,
    
    error = ErrorRed,
    onError = White
)

@Composable
fun TicketeraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
