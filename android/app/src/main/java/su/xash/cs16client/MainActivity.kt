package su.xash.cs16client

import androidx.annotation.StringRes

class MainActivity : LauncherActivity() {

	@get:StringRes
	override val titleRes: Int = R.string.app_name

	@get:StringRes
	override val subtitleRes: Int = R.string.subtitle_launcher
}
