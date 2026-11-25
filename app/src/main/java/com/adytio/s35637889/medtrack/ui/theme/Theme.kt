package com.adytio.s35637889.medtrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary              = MedBlue,
    onPrimary            = MedBlueOnPrimary,
    primaryContainer     = MedBlueCont,
    onPrimaryContainer   = MedBlueOnCont,
    secondary            = MedSecondary,
    secondaryContainer   = MedSecondaryCont,
    onSecondaryContainer = MedSecondaryOnC,
    tertiary             = MedGreen,
    onTertiary           = MedGreenOn,
    tertiaryContainer    = MedGreenCont,
    error                = MedError,
    errorContainer       = MedErrorCont,
    onErrorContainer     = MedOnErrorCont,
    background           = MedBackground,
    surface              = MedSurface,
    surfaceVariant       = MedSurfaceVar,
    onSurfaceVariant     = MedOnSurfaceVar,
)

private val DarkColors = darkColorScheme(
    primary              = MedBlueDark,
    onPrimary            = MedBlueOnPrimaryDark,
    primaryContainer     = MedBlueContDark,
    onPrimaryContainer   = MedBlueOnContDark,
    secondary            = MedSecondaryDark,
    secondaryContainer   = MedSecondaryContDark,
    onSecondaryContainer = MedSecondaryOnCDark,
    tertiary             = MedGreenDark,
    onTertiary           = MedGreenOnDark,
    tertiaryContainer    = MedGreenContDark,
    error                = MedErrorDark,
    errorContainer       = MedErrorContDark,
    onErrorContainer     = MedOnErrorContDark,
    background           = MedBackgroundDark,
    surface              = MedSurfaceDark,
    surfaceVariant       = MedSurfaceVarDark,
    onSurfaceVariant     = MedOnSurfaceVarDark,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
//fun MedTrackTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    fun MedTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}