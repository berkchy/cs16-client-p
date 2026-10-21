package su.xash.cs16client.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Fallback palette, used when the system does not provide Material You colors
 * (Android 11 and older) or when the player turns dynamic colors off.
 */
private val LightColors = lightColorScheme(
	primary = Color(0xFF00658E),
	onPrimary = Color(0xFFFFFFFF),
	primaryContainer = Color(0xFFC7E7FF),
	onPrimaryContainer = Color(0xFF001E2E),
	secondary = Color(0xFF4F616E),
	onSecondary = Color(0xFFFFFFFF),
	secondaryContainer = Color(0xFFD2E5F5),
	onSecondaryContainer = Color(0xFF0B1D29),
	tertiary = Color(0xFF615A7C),
	onTertiary = Color(0xFFFFFFFF),
	tertiaryContainer = Color(0xFFE7DEFF),
	onTertiaryContainer = Color(0xFF1D1735),
	error = Color(0xFFBA1A1A),
	onError = Color(0xFFFFFFFF),
	errorContainer = Color(0xFFFFDAD6),
	onErrorContainer = Color(0xFF410002),
	background = Color(0xFFFBFCFE),
	onBackground = Color(0xFF191C1E),
	surface = Color(0xFFFBFCFE),
	onSurface = Color(0xFF191C1E),
	surfaceVariant = Color(0xFFDDE3EA),
	onSurfaceVariant = Color(0xFF41484D),
	outline = Color(0xFF71787E),
	outlineVariant = Color(0xFFC1C7CE)
)

private val DarkColors = darkColorScheme(
	primary = Color(0xFF85CFFF),
	onPrimary = Color(0xFF00344C),
	primaryContainer = Color(0xFF004C6C),
	onPrimaryContainer = Color(0xFFC7E7FF),
	secondary = Color(0xFFB6C9D8),
	onSecondary = Color(0xFF21323E),
	secondaryContainer = Color(0xFF374955),
	onSecondaryContainer = Color(0xFFD2E5F5),
	tertiary = Color(0xFFCBC1E9),
	onTertiary = Color(0xFF332C4C),
	tertiaryContainer = Color(0xFF4A4263),
	onTertiaryContainer = Color(0xFFE7DEFF),
	error = Color(0xFFFFB4AB),
	onError = Color(0xFF690005),
	errorContainer = Color(0xFF93000A),
	onErrorContainer = Color(0xFFFFDAD6),
	background = Color(0xFF191C1E),
	onBackground = Color(0xFFE1E2E5),
	surface = Color(0xFF191C1E),
	onSurface = Color(0xFFE1E2E5),
	surfaceVariant = Color(0xFF41484D),
	onSurfaceVariant = Color(0xFFC1C7CE),
	outline = Color(0xFF8B9198),
	outlineVariant = Color(0xFF41484D)
)

/**
 * Material 3 theme. On Android 12 and newer the colors are taken from the
 * wallpaper, which is what "Material You" means.
 */
@Composable
fun CS16ClientTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val context = LocalContext.current

	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

		darkTheme -> DarkColors
		else -> LightColors
	}

	MaterialTheme(
		colorScheme = colorScheme,
		content = content
	)
}
