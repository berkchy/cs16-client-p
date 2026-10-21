package su.xash.cs16client.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import su.xash.cs16client.GameDir
import su.xash.cs16client.R
import su.xash.cs16client.ThemeMode

private val ArgumentPresets = listOf(
	"-dev 3 -log",
	"-dev 4 -log",
	"-console -dev 3 -log",
	"-dev 3 -log -dll @yapb"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LauncherScreen(
	title: String,
	subtitle: String,
	gameDir: GameDir,
	onGameDirChange: (GameDir) -> Unit,
	argv: String,
	onArgvChange: (String) -> Unit,
	commandLine: String,
	loadBots: Boolean,
	onLoadBotsChange: (Boolean) -> Unit,
	themeMode: ThemeMode,
	onThemeModeChange: (ThemeMode) -> Unit,
	dynamicColor: Boolean,
	onDynamicColorChange: (Boolean) -> Unit,
	enginePackage: String?,
	engineMissing: Boolean,
	onDismissEngineMissing: () -> Unit,
	onPlay: () -> Unit,
	onReset: () -> Unit,
	onCopy: () -> Unit,
	onPaste: () -> Unit,
	onInstallEngine: () -> Unit
) {
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()
	val copiedMessage = stringResource(R.string.copied_to_clipboard)

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(title, style = MaterialTheme.typography.titleLarge)
						Text(
							subtitle,
							style = MaterialTheme.typography.labelMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			)
		},
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 16.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			GameCard(
				gameDir = gameDir,
				onGameDirChange = onGameDirChange
			)

			CommandLineCard(
				argv = argv,
				onArgvChange = onArgvChange,
				commandLine = commandLine,
				loadBots = loadBots,
				onLoadBotsChange = onLoadBotsChange,
				onReset = onReset,
				onCopy = {
					onCopy()
					scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
				},
				onPaste = onPaste
			)

			AppearanceCard(
				themeMode = themeMode,
				onThemeModeChange = onThemeModeChange,
				dynamicColor = dynamicColor,
				onDynamicColorChange = onDynamicColorChange
			)

			EngineCard(
				enginePackage = enginePackage,
				onInstallEngine = onInstallEngine
			)

			Button(
				onClick = onPlay,
				modifier = Modifier
					.fillMaxWidth()
					.height(60.dp),
				shape = MaterialTheme.shapes.large
			) {
				Icon(Icons.Filled.PlayArrow, contentDescription = null)
				Spacer(Modifier.width(8.dp))
				Text(
					stringResource(R.string.action_play),
					style = MaterialTheme.typography.titleMedium
				)
			}

			Text(
				stringResource(R.string.play_help),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.fillMaxWidth()
			)
		}
	}

	if (engineMissing) {
		AlertDialog(
			onDismissRequest = onDismissEngineMissing,
			icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
			title = { Text(stringResource(R.string.engine_missing_title)) },
			text = { Text(stringResource(R.string.engine_missing_help)) },
			confirmButton = {
				TextButton(
					onClick = {
						onDismissEngineMissing()
						onInstallEngine()
					}
				) {
					Text(stringResource(R.string.action_install))
				}
			},
			dismissButton = {
				TextButton(onClick = onDismissEngineMissing) {
					Text(stringResource(R.string.action_close))
				}
			}
		)
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameCard(
	gameDir: GameDir,
	onGameDirChange: (GameDir) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				stringResource(R.string.section_game),
				style = MaterialTheme.typography.titleMedium
			)

			FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				GameDir.entries.forEach { dir ->
					FilterChip(
						selected = gameDir == dir,
						onClick = { onGameDirChange(dir) },
						label = { Text(stringResource(dir.labelRes)) }
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CommandLineCard(
	argv: String,
	onArgvChange: (String) -> Unit,
	commandLine: String,
	loadBots: Boolean,
	onLoadBotsChange: (Boolean) -> Unit,
	onReset: () -> Unit,
	onCopy: () -> Unit,
	onPaste: () -> Unit
) {
	val focusManager = LocalFocusManager.current

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				stringResource(R.string.section_command_line),
				style = MaterialTheme.typography.titleMedium
			)

			Text(
				stringResource(R.string.help_arguments),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)

			OutlinedTextField(
				value = argv,
				onValueChange = onArgvChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text(stringResource(R.string.label_arguments)) },
				placeholder = { Text(stringResource(R.string.hint_arguments)) },
				leadingIcon = { Icon(Icons.Filled.Terminal, contentDescription = null) },
				trailingIcon = {
					Row {
						IconButton(onClick = onPaste) {
							Icon(
								Icons.Filled.ContentPaste,
								contentDescription = stringResource(R.string.action_paste)
							)
						}
						IconButton(onClick = onCopy) {
							Icon(
								Icons.Filled.ContentCopy,
								contentDescription = stringResource(R.string.action_copy)
							)
						}
						IconButton(onClick = onReset) {
							Icon(
								Icons.Filled.Refresh,
								contentDescription = stringResource(R.string.action_reset)
							)
						}
					}
				},
				singleLine = true,
				textStyle = MaterialTheme.typography.bodyMedium.copy(
					fontFamily = FontFamily.Monospace
				),
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
			)

			Text(
				stringResource(R.string.effective_command_line, commandLine),
				style = MaterialTheme.typography.bodySmall.copy(
					fontFamily = FontFamily.Monospace
				),
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)

			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				ArgumentPresets.forEach { preset ->
					FilterChip(
						selected = argv.trim() == preset,
						onClick = { onArgvChange(preset) },
						label = {
							Text(preset, style = MaterialTheme.typography.labelSmall)
						}
					)
				}
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(Modifier.weight(1f)) {
					Text(
						stringResource(R.string.label_bots),
						style = MaterialTheme.typography.bodyLarge
					)
					Text(
						stringResource(R.string.help_bots),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				Switch(checked = loadBots, onCheckedChange = onLoadBotsChange)
			}
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppearanceCard(
	themeMode: ThemeMode,
	onThemeModeChange: (ThemeMode) -> Unit,
	dynamicColor: Boolean,
	onDynamicColorChange: (Boolean) -> Unit
) {
	val dynamicColorSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				stringResource(R.string.section_appearance),
				style = MaterialTheme.typography.titleMedium
			)

			Text(
				stringResource(R.string.label_theme),
				style = MaterialTheme.typography.bodyMedium
			)

			FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				ThemeMode.entries.forEach { mode ->
					FilterChip(
						selected = themeMode == mode,
						onClick = { onThemeModeChange(mode) },
						label = { Text(stringResource(mode.labelRes())) }
					)
				}
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(Modifier.weight(1f)) {
					Text(
						stringResource(R.string.label_dynamic_color),
						style = MaterialTheme.typography.bodyLarge
					)
					Text(
						stringResource(
							if (dynamicColorSupported) R.string.help_dynamic_color
							else R.string.dynamic_color_unsupported
						),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				Switch(
					checked = dynamicColor && dynamicColorSupported,
					enabled = dynamicColorSupported,
					onCheckedChange = onDynamicColorChange
				)
			}
		}
	}
}

@Composable
private fun EngineCard(
	enginePackage: String?,
	onInstallEngine: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		)
	) {
		if (enginePackage != null) {
			ListItem(
				headlineContent = {
					Text(stringResource(R.string.engine_installed, enginePackage))
				},
				leadingContent = {
					Icon(
						Icons.Filled.CheckCircle,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary
					)
				}
			)
		} else {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						Icons.Filled.Warning,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.error
					)
					Spacer(Modifier.width(8.dp))
					Text(
						stringResource(R.string.engine_missing),
						style = MaterialTheme.typography.bodyLarge
					)
				}
				Text(
					stringResource(R.string.engine_missing_help),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				TextButton(onClick = onInstallEngine) {
					Icon(Icons.Filled.Download, contentDescription = null)
					Spacer(Modifier.width(8.dp))
					Text(stringResource(R.string.action_install))
				}
			}
		}
	}
}

private fun ThemeMode.labelRes(): Int = when (this) {
	ThemeMode.SYSTEM -> R.string.theme_system
	ThemeMode.LIGHT -> R.string.theme_light
	ThemeMode.DARK -> R.string.theme_dark
}
