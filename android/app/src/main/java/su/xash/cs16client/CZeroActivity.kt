package su.xash.cs16client

import androidx.annotation.StringRes

class CZeroActivity : LauncherActivity() {

	override val gameDir: String = "czero"

	@get:StringRes
	override val titleRes: Int = R.string.app_name_czero

	@get:StringRes
	override val subtitleRes: Int = R.string.subtitle_czero
}
