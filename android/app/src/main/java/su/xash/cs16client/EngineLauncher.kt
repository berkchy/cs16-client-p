package su.xash.cs16client

import android.content.Context
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * Bridge to the Xash3D FWGS engine application.
 *
 * The engine is a separate APK: we hand it the game directory, the folder that
 * holds the native client libraries and the command line that has to be parsed
 * by the engine itself (the "-dev 3 -log" style switches).
 */
object EngineLauncher {

	const val ENGINE_ACTIVITY = "su.xash.engine.XashActivity"
	const val DOWNLOAD_URL = "https://github.com/FWGS/xash3d-fwgs/releases/tag/continuous"

	/** Test builds first, the release build second, just like the old launcher did. */
	private val ENGINE_PACKAGES = listOf("su.xash.engine.test", "su.xash.engine")

	fun findEnginePackage(context: Context): String? {
		val packageManager = context.packageManager
		for (candidate in ENGINE_PACKAGES) {
			try {
				packageManager.getPackageInfo(candidate, 0)
				return candidate
			} catch (_: PackageManager.NameNotFoundException) {
				// Not installed, try the next one.
			}
		}
		return null
	}

	fun launch(context: Context, enginePackage: String, gameDir: String, commandLine: String) {
		val intent = Intent()
			.setComponent(ComponentName(enginePackage, ENGINE_ACTIVITY))
			.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
			.putExtra("gamedir", gameDir)
			.putExtra("gamelibdir", context.applicationInfo.nativeLibraryDir)
			.putExtra("argv", commandLine)
			.putExtra("package", context.packageName)

		context.startActivity(intent)
	}

	fun openDownloadPage(context: Context) {
		context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DOWNLOAD_URL)))
	}
}
