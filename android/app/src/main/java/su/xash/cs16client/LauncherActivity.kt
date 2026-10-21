package su.xash.cs16client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import su.xash.cs16client.ui.CS16ClientTheme
import su.xash.cs16client.ui.LauncherScreen

/**
 * Common launcher UI: a command line the player can edit, a couple of switches
 * and a big play button that starts the Xash3D engine with those arguments.
 */
abstract class LauncherActivity : ComponentActivity() {

	/** Game directory inside the engine, "cstrike" or "czero". */
	protected abstract val gameDir: String

	@get:StringRes
	protected abstract val titleRes: Int

	@get:StringRes
	protected abstract val subtitleRes: Int

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			val model: LauncherViewModel = viewModel(factory = LauncherViewModel.factory(gameDir))

			val darkTheme = when (model.themeMode) {
				ThemeMode.SYSTEM -> isSystemInDarkTheme()
				ThemeMode.LIGHT -> false
				ThemeMode.DARK -> true
			}

			CS16ClientTheme(
				darkTheme = darkTheme,
				dynamicColor = model.dynamicColor
			) {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					LauncherScreen(
						title = stringResource(titleRes),
						subtitle = stringResource(subtitleRes),
						argv = model.argv,
						onArgvChange = model::updateArgv,
						commandLine = model.commandLine,
						loadBots = model.loadBots,
						onLoadBotsChange = model::updateLoadBots,
						themeMode = model.themeMode,
						onThemeModeChange = model::updateThemeMode,
						dynamicColor = model.dynamicColor,
						onDynamicColorChange = model::updateDynamicColor,
						enginePackage = model.enginePackage,
						engineMissing = model.engineMissing,
						onDismissEngineMissing = model::dismissEngineMissing,
						onPlay = model::play,
						onReset = model::resetArgv,
						onCopy = { copyToClipboard(model.commandLine) },
						onPaste = { pasteFromClipboard()?.let(model::updateArgv) },
						onInstallEngine = { EngineLauncher.openDownloadPage(this) }
					)
				}
			}
		}
	}

	private fun copyToClipboard(text: String) {
		val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
		clipboard?.setPrimaryClip(ClipData.newPlainText("argv", text))
	}

	private fun pasteFromClipboard(): String? {
		val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
		return clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
	}
}
