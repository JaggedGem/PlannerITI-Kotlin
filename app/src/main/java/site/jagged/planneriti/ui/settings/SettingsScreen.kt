package site.jagged.planneriti.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import site.jagged.planneriti.ui.components.AppCard
import site.jagged.planneriti.ui.components.AppDivider
import site.jagged.planneriti.ui.components.AppSectionTitle
import site.jagged.planneriti.ui.components.SettingRow
import site.jagged.planneriti.ui.components.StatusChip
import site.jagged.planneriti.ui.theme.Background
import site.jagged.planneriti.ui.theme.OnSurfaceMuted
import site.jagged.planneriti.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun SettingsScreen(
    onOpenGrades: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showGroupPicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var editPeriod by remember { mutableStateOf<CustomPeriodSetting?>(null) }
    var showPeriodDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.checkForUpdates(manual = false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppSectionTitle("Settings", "Manage your account, schedule and app behavior")

        AccountCard(
            uiState = uiState,
            onRefresh = viewModel::refreshAccount,
            onLogout = viewModel::logout,
            onDelete = { showDeleteAccount = true }
        )

        IdentityCard(obfuscatedIdnp = maskIdnp(uiState.idnp), onOpenGrades = onOpenGrades)

        AppCard {
            SettingRow(
                title = "Language",
                value = languageLabel(uiState.language),
                subtitle = "App interface language",
                onClick = { showLanguagePicker = true },
                trailing = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = OnSurfaceMuted) }
            )
        }

        ScheduleCard(
            uiState = uiState,
            onGroupClick = { showGroupPicker = true },
            onSubgroupSelect = viewModel::setSubgroup,
            onRefresh = viewModel::refreshSchedule,
            onClearCache = viewModel::clearScheduleCache
        )

        UpdateCard(
            uiState = uiState,
            onCheckNow = { viewModel.checkForUpdates(manual = true) },
            onRemindLater = viewModel::dismissUpdate
        )

        NotificationCard(uiState = uiState, onSave = viewModel::saveNotificationSettings)

        CustomPeriodsCard(
            periods = uiState.customPeriods,
            onAdd = {
                editPeriod = null
                showPeriodDialog = true
            },
            onEdit = {
                editPeriod = it
                showPeriodDialog = true
            },
            onDelete = viewModel::deleteCustomPeriod,
            onToggle = { period, enabled -> viewModel.updateCustomPeriod(period.copy(isEnabled = enabled)) }
        )

        uiState.error?.let { ErrorBanner(it) }
    }

    if (showGroupPicker) {
        OptionPickerDialog(
            title = "Select group",
            options = uiState.groups,
            selectedId = uiState.selectedGroupId,
            id = { it.id },
            label = { it.name },
            supportingText = { it.teacherName },
            onDismiss = { showGroupPicker = false },
            onSelect = {
                viewModel.selectGroup(it)
                showGroupPicker = false
            }
        )
    }

    if (showLanguagePicker) {
        data class LanguageOption(val id: String, val label: String)
        OptionPickerDialog(
            title = "Select language",
            options = listOf(
                LanguageOption("en", "English"),
                LanguageOption("ro", "Romanian"),
                LanguageOption("ru", "Russian")
            ),
            selectedId = uiState.language,
            id = { it.id },
            label = { it.label },
            supportingText = { null },
            onDismiss = { showLanguagePicker = false },
            onSelect = {
                viewModel.setLanguage(it.id)
                showLanguagePicker = false
            }
        )
    }

    if (showDeleteAccount) {
        var password by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeleteAccount = false },
            title = { Text("Delete account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your password to confirm account deletion request.")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        label = { Text("Password") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccount(password)
                    showDeleteAccount = false
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteAccount = false }) { Text("Cancel") } }
        )
    }

    if (showPeriodDialog) {
        CustomPeriodDialog(
            initial = editPeriod,
            onDismiss = { showPeriodDialog = false },
            onSave = { period ->
                if (editPeriod == null) viewModel.addCustomPeriod(period) else viewModel.updateCustomPeriod(period)
                showPeriodDialog = false
            }
        )
    }
}

@Composable
private fun AccountCard(
    uiState: SettingsUiState,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard {
        AppSectionTitle("Account")
        if (!uiState.account.isAuthenticated) {
            Text("Not signed in", color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Sign in to sync account-only data.", color = OnSurfaceMuted)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.account.email?.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(uiState.account.email.orEmpty(), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (uiState.account.isVerified) {
                        StatusChip("Verified")
                    } else {
                        Text("Email not verified", color = Color(0xFFFFB020))
                    }
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh account", tint = Color.White)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Logout")
                }
                Button(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun IdentityCard(obfuscatedIdnp: String, onOpenGrades: () -> Unit) {
    AppCard {
        AppSectionTitle("Identity")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Primary)
            Spacer(Modifier.width(8.dp))
            Text("IDNP", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text(obfuscatedIdnp, color = OnSurfaceMuted)
        }
        Text("For safety, IDNP editing is available in Grades only.", color = OnSurfaceMuted)
        OutlinedButton(onClick = onOpenGrades) { Text("Manage IDNP in Grades") }
    }
}

@Composable
private fun ScheduleCard(
    uiState: SettingsUiState,
    onGroupClick: () -> Unit,
    onSubgroupSelect: (String) -> Unit,
    onRefresh: () -> Unit,
    onClearCache: () -> Unit
) {
    AppCard {
        AppSectionTitle("Schedule")
        SettingRow(title = "Group", value = uiState.selectedGroupName, onClick = onGroupClick)
        AppDivider()
        Text("Subgroup", color = Color.White, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = uiState.subgroup == "Subgroup 1",
                onClick = { onSubgroupSelect("Subgroup 1") },
                label = { Text("Subgroup 1") }
            )
            FilterChip(
                selected = uiState.subgroup == "Subgroup 2",
                onClick = { onSubgroupSelect("Subgroup 2") },
                label = { Text("Subgroup 2") }
            )
        }
        AppDivider()
        Text(
            "Last refresh: ${uiState.lastScheduleRefreshEpoch?.let(::formatDateTime) ?: "never"}",
            color = OnSurfaceMuted
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onRefresh, enabled = !uiState.isRefreshingSchedule) {
                Text(if (uiState.isRefreshingSchedule) "Refreshing..." else "Refresh")
            }
            OutlinedButton(onClick = onClearCache) { Text("Clear Cache") }
        }
    }
}

@Composable
private fun UpdateCard(
    uiState: SettingsUiState,
    onCheckNow: () -> Unit,
    onRemindLater: (String) -> Unit
) {
    AppCard {
        AppSectionTitle("App Updates")
        SettingRow(
            title = "Current version",
            value = uiState.currentVersion,
            subtitle = "Checks once per day automatically"
        )
        Button(onClick = onCheckNow, enabled = !uiState.isCheckingForUpdate) {
            Text(if (uiState.isCheckingForUpdate) "Checking..." else "Check now")
        }
        uiState.updateInfo?.let { update ->
            Text("New version ${update.latestVersion} available", color = Color(0xFF30D158), fontWeight = FontWeight.SemiBold)
            Text(update.releaseNotes, color = OnSurfaceMuted, maxLines = 4, overflow = TextOverflow.Ellipsis)
            OutlinedButton(onClick = { onRemindLater(update.latestVersion) }) { Text("Remind me later") }
        }
        uiState.updateMessage?.let { Text(it, color = OnSurfaceMuted) }
    }
}

@Composable
private fun NotificationCard(uiState: SettingsUiState, onSave: (NotificationSettings) -> Unit) {
    AppCard {
        AppSectionTitle("Notifications")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable reminders", color = Color.White, modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.notificationSettings.enabled,
                onCheckedChange = { onSave(uiState.notificationSettings.copy(enabled = it)) }
            )
        }
        if (uiState.notificationSettings.enabled) {
            ReminderRow(
                label = "Exams",
                value = uiState.notificationSettings.examReminderDays,
                onChange = { onSave(uiState.notificationSettings.copy(examReminderDays = it)) }
            )
            ReminderRow(
                label = "Tests",
                value = uiState.notificationSettings.testReminderDays,
                onChange = { onSave(uiState.notificationSettings.copy(testReminderDays = it)) }
            )
            ReminderRow(
                label = "Quizzes",
                value = uiState.notificationSettings.quizReminderDays,
                onChange = { onSave(uiState.notificationSettings.copy(quizReminderDays = it)) }
            )
            ReminderRow(
                label = "Projects",
                value = uiState.notificationSettings.projectReminderDays,
                onChange = { onSave(uiState.notificationSettings.copy(projectReminderDays = it)) }
            )
        }
    }
}

@Composable
private fun CustomPeriodsCard(
    periods: List<CustomPeriodSetting>,
    onAdd: () -> Unit,
    onEdit: (CustomPeriodSetting) -> Unit,
    onDelete: (String) -> Unit,
    onToggle: (CustomPeriodSetting, Boolean) -> Unit
) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppSectionTitle("Custom Periods", "Add personal time blocks")
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add custom period", tint = Color.White)
            }
        }
        if (periods.isEmpty()) {
            Text("No custom periods configured.", color = OnSurfaceMuted)
        } else {
            periods.forEach { period ->
                AppDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(android.graphics.Color.parseColor(period.colorHex)), CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(period.name, color = Color.White)
                        Text("${period.startTime} - ${period.endTime}", color = OnSurfaceMuted)
                    }
                    Switch(checked = period.isEnabled, onCheckedChange = { onToggle(period, it) })
                    IconButton(onClick = { onEdit(period) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit period", tint = Color.White)
                    }
                    IconButton(onClick = { onDelete(period.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete period", tint = Color(0xFFFF7A7A))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderRow(label: String, value: Int, onChange: (Int) -> Unit) {
    SettingRow(
        title = label,
        value = "$value day(s)",
        trailing = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { if (value > 1) onChange(value - 1) }) { Text("-") }
                TextButton(onClick = { onChange(value + 1) }) { Text("+") }
            }
        }
    )
}

@Composable
private fun ErrorBanner(message: String) {
    AppCard {
        Text(message, color = Color(0xFFFFB3B3), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun <T> OptionPickerDialog(
    title: String,
    options: List<T>,
    selectedId: String,
    id: (T) -> String,
    label: (T) -> String,
    supportingText: (T) -> String?,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    val isSelected = id(option) == selectedId

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSelect(option) }
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(label(option), color = if (isSelected) Primary else Color.White)
                            supportingText(option)?.let { Text(it, color = OnSurfaceMuted) }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
private fun CustomPeriodDialog(
    initial: CustomPeriodSetting?,
    onDismiss: () -> Unit,
    onSave: (CustomPeriodSetting) -> Unit
) {
    var name by remember(initial) { mutableStateOf(initial?.name.orEmpty()) }
    var start by remember(initial) { mutableStateOf(initial?.startTime ?: "08:00") }
    var end by remember(initial) { mutableStateOf(initial?.endTime ?: "08:45") }
    var days by remember(initial) { mutableStateOf(initial?.daysOfWeek?.joinToString(",").orEmpty()) }
    var color by remember(initial) { mutableStateOf(initial?.colorHex ?: "#2C3DCD") }
    var enabled by remember(initial) { mutableStateOf(initial?.isEnabled ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add custom period" else "Edit custom period") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Start (HH:mm)") }, singleLine = true)
                OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("End (HH:mm)") }, singleLine = true)
                OutlinedTextField(value = days, onValueChange = { days = it }, label = { Text("Days (1,2,3...)") }, singleLine = true)
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color (#RRGGBB)") }, singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enabled", modifier = Modifier.weight(1f))
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedDays = days.split(',').mapNotNull { it.trim().toIntOrNull() }.filter { it in 1..7 }
                onSave(
                    CustomPeriodSetting(
                        id = initial?.id ?: UUID.randomUUID().toString(),
                        name = name.ifBlank { "Custom Period" },
                        startTime = start,
                        endTime = end,
                        daysOfWeek = parsedDays,
                        colorHex = if (color.startsWith("#")) color else "#2C3DCD",
                        isEnabled = enabled
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun languageLabel(language: String): String = when (language) {
    "ro" -> "Romanian"
    "ru" -> "Russian"
    else -> "English"
}

private fun maskIdnp(idnp: String?): String {
    if (idnp.isNullOrBlank()) return "Not set"
    if (idnp.length < 8) return "Saved"
    return "${idnp.take(4)}••••••${idnp.takeLast(3)}"
}

private fun formatDateTime(epochMillis: Long): String {
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(epochMillis))
    }.getOrDefault("-")
}