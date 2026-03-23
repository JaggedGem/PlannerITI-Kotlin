package site.jagged.planneriti.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import site.jagged.planneriti.ui.settings.UpdateInfo
import javax.inject.Inject
import javax.inject.Singleton

private data class GithubRelease(
    val tag_name: String,
    val body: String?,
    val html_url: String,
    val published_at: String,
    val prerelease: Boolean
)

@Singleton
class UpdateRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val settingsRepository: SettingsRepository
) {
    private val gson = Gson()

    private val githubReleasesUrl = "https://api.github.com/repos/JaggedGem/PlannerITI/releases"
    private val remindLaterDays = 7

    suspend fun checkForUpdate(forceCheck: Boolean = false): UpdateInfo? {
        if (!forceCheck && !shouldCheckToday()) return null

        settingsRepository.setLastUpdateCheck(System.currentTimeMillis())
        val release = fetchLatestRelease(productionOnly = true) ?: return null

        val latestVersion = release.tag_name
        if (compareVersions(getCurrentVersion(), latestVersion) >= 0) return null

        if (!forceCheck && isVersionDismissed(latestVersion)) return null

        return UpdateInfo(
            isAvailable = true,
            currentVersion = getCurrentVersion(),
            latestVersion = latestVersion,
            releaseNotes = release.body ?: "No release notes available.",
            releaseUrl = release.html_url,
            publishedAt = release.published_at
        )
    }

    suspend fun manualCheckForUpdate(): UpdateInfo? {
        clearDismissedVersion()
        return checkForUpdate(forceCheck = true)
    }

    suspend fun dismissVersion(version: String) {
        val until = System.currentTimeMillis() + remindLaterDays * 24L * 60L * 60L * 1000L
        settingsRepository.setUpdateDismissed(version, until)
    }

    suspend fun clearDismissedVersion() = settingsRepository.clearUpdateDismissed()

    fun getCurrentVersion(): String {
        return runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        }.getOrDefault("1.0")
    }

    private suspend fun shouldCheckToday(): Boolean {
        val now = System.currentTimeMillis()
        val last = settingsRepository.lastUpdateCheckEpoch().firstOrNull() ?: return true
        val dayMillis = 24L * 60L * 60L * 1000L
        return now - last >= dayMillis
    }

    private suspend fun isVersionDismissed(version: String): Boolean {
        val dismissedVersion = settingsRepository.dismissedUpdateVersion().firstOrNull() ?: return false
        val dismissedUntil = settingsRepository.dismissedUpdateUntilEpoch().firstOrNull() ?: return false
        return dismissedVersion == version && dismissedUntil > System.currentTimeMillis()
    }

    private fun compareVersions(current: String, latest: String): Int {
        fun normalize(v: String) = v.removePrefix("v").split("-").first()
        val c = normalize(current).split(".").map { it.toIntOrNull() ?: 0 }
        val l = normalize(latest).split(".").map { it.toIntOrNull() ?: 0 }
        val max = maxOf(c.size, l.size)
        for (i in 0 until max) {
            val cv = c.getOrElse(i) { 0 }
            val lv = l.getOrElse(i) { 0 }
            if (cv != lv) return cv.compareTo(lv)
        }
        return 0
    }

    private fun fetchLatestRelease(productionOnly: Boolean): GithubRelease? {
        return runCatching {
            val request = Request.Builder()
                .url(githubReleasesUrl)
                .header("Accept", "application/vnd.github+json")
                .build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body.string()
                if (body.isBlank()) return null
                val listType = object : TypeToken<List<GithubRelease>>() {}.type
                val releases: List<GithubRelease> = gson.fromJson(body, listType)
                val filtered = if (productionOnly) releases.filter { !it.prerelease } else releases
                filtered.firstOrNull()
            }
        }.getOrNull()
    }
}


