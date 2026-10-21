package su.xash.cs16client

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

enum class ThemeMode(val storageName: String) {
	SYSTEM("system"),
	LIGHT("light"),
	DARK("dark");

	companion object {
		fun fromStorage(value: String?): ThemeMode =
			entries.firstOrNull { it.storageName == value } ?: SYSTEM
	}
}

/**
 * Holds everything the launcher screen shows and keeps it in the preferences,
 * so the command line survives a restart of the app.
 */
class LauncherViewModel(
	application: Application,
	private val gameDir: String
) : AndroidViewModel(application) {

	private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

	var argv: String by mutableStateOf(prefs.getString(key(KEY_ARGV), DEFAULT_ARGS) ?: DEFAULT_ARGS)
		private set

	var loadBots: Boolean by mutableStateOf(prefs.getBoolean(key(KEY_BOTS), true))
		private set

	var dynamicColor: Boolean by mutableStateOf(prefs.getBoolean(key(KEY_DYNAMIC_COLOR), true))
		private set

	var themeMode: ThemeMode by mutableStateOf(ThemeMode.fromStorage(prefs.getString(key(KEY_THEME), null)))
		private set

	var enginePackage: String? by mutableStateOf(null)
		private set

	var engineMissing: Boolean by mutableStateOf(false)
		private set

	init {
		enginePackage = EngineLauncher.findEnginePackage(application)
	}

	/** The command line that is really handed over to the engine. */
	val commandLine: String
		get() = buildString {
			val typed = argv.trim()
			if (typed.isNotEmpty()) {
				append(typed)
			}
			if (loadBots && !typed.contains("-dll")) {
				if (isNotEmpty()) {
					append(' ')
				}
				append(BOTS_ARGUMENT)
			}
		}

	fun updateArgv(value: String) {
		argv = value
			prefs.edit().putString(key(KEY_ARGV), value).apply()
	}

	fun updateLoadBots(value: Boolean) {
		loadBots = value
			prefs.edit().putBoolean(key(KEY_BOTS), value).apply()
	}

	fun updateDynamicColor(value: Boolean) {
		dynamicColor = value
			prefs.edit().putBoolean(key(KEY_DYNAMIC_COLOR), value).apply()
	}

	fun updateThemeMode(value: ThemeMode) {
		themeMode = value
			prefs.edit().putString(key(KEY_THEME), value.storageName).apply()
	}

	fun resetArgv() {
		updateArgv(DEFAULT_ARGS)
	}

	fun play() {
		val engine = enginePackage ?: EngineLauncher.findEnginePackage(getApplication<Application>()).also {
			enginePackage = it
		}

		if (engine == null) {
			engineMissing = true
			return
		}

		EngineLauncher.launch(getApplication(), engine, gameDir, commandLine)
	}

	fun dismissEngineMissing() {
		engineMissing = false
	}

	private fun key(name: String) = "${name}_$gameDir"

	companion object {
		private const val PREFS_NAME = "cs16client"
		private const val KEY_ARGV = "argv"
		private const val KEY_BOTS = "bots"
		private const val KEY_DYNAMIC_COLOR = "dynamic_color"
		private const val KEY_THEME = "theme"

		const val DEFAULT_ARGS = "-dev 3 -log"
		const val BOTS_ARGUMENT = "-dll @yapb"

		fun factory(gameDir: String) = viewModelFactory {
			initializer {
				val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
				LauncherViewModel(application, gameDir)
			}
		}
	}
}
