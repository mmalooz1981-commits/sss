package com.example.audio

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.data.local.LinaDatabase
import com.example.data.local.VoiceSettingsEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class AudioDownloadProgress(
    val unitNumber: Int, // 0 = All curriculum units
    val isRunning: Boolean,
    val completedCount: Int,
    val totalCount: Int,
    val failedCount: Int,
    val currentTitleAr: String,
    val statusMessageAr: String
) {
    val percent: Int
        get() = if (totalCount <= 0) 0 else ((completedCount * 100) / totalCount).coerceIn(0, 100)
}

/**
 * WorkManager CoroutineWorker for resilient background downloads of unit or full-curriculum audio packs.
 * Automatically skips already-verified cached files (preventing duplicate downloads & supporting Pause/Resume).
 */
class AudioDownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val unitNumber = inputData.getInt(KEY_UNIT_NUMBER, 0)
        val items = if (unitNumber == 0) {
            CurriculumAudioCatalog.allItems
        } else {
            CurriculumAudioCatalog.itemsForUnit(unitNumber)
        }

        if (items.isEmpty()) return Result.success()

        val db = LinaDatabase.getInstance(applicationContext)
        val settings = db.voiceSettingsDao().getSettingsOnce() ?: VoiceSettingsEntity()
        val accent = VoiceAccent.fromCode(settings.accentCode)
        val defaultPersona = VoicePersona.fromId(settings.personaId)
        val speechMode = SpeechMode.fromId(settings.speechModeId)
        val enginePref = VoiceEnginePreference.fromId(settings.enginePreferenceId)

        val tempWordSpeaker = WordSpeaker(applicationContext)
        val audioManager = HybridAudioManager(
            context = applicationContext,
            cachedAudioDao = db.cachedAudioDao(),
            voiceSettingsDao = db.voiceSettingsDao(),
            wordSpeaker = tempWordSpeaker
        )

        var completed = 0
        var failed = 0
        var consecutiveNetworkFailures = 0

        try {
            setProgress(
                buildProgressData(
                    unitNumber = unitNumber,
                    completed = 0,
                    total = items.size,
                    failed = 0,
                    title = "جاري تجهيز قائمة الأصوات..."
                )
            )

            for (item in items) {
                if (isStopped) {
                    return Result.success(
                        buildProgressData(unitNumber, completed, items.size, failed, "تم إيقاف التنزيل مؤقتاً")
                    )
                }

                if (!audioManager.isOnline()) {
                    return Result.retry()
                }

                val personaForItem = item.preferredPersona ?: defaultPersona
                val outcome = audioManager.ensureItemCached(
                    item = item,
                    accent = accent,
                    personaOverride = personaForItem,
                    speechMode = speechMode,
                    enginePref = enginePref,
                    endpointUrl = settings.cloudTtsEndpointUrl
                )

                when (outcome) {
                    is CacheItemOutcome.Ready -> {
                        completed++
                        consecutiveNetworkFailures = 0
                    }
                    is CacheItemOutcome.Failed -> {
                        failed++
                        consecutiveNetworkFailures++
                        Log.w(TAG, "Audio download failed for '${item.textEn}': ${outcome.reasonAr}")
                        if (consecutiveNetworkFailures >= 5) {
                            return Result.failure(
                                buildProgressData(
                                    unitNumber = unitNumber,
                                    completed = completed,
                                    total = items.size,
                                    failed = failed,
                                    title = outcome.reasonAr
                                )
                            )
                        }
                    }
                }

                setProgress(
                    buildProgressData(
                        unitNumber = unitNumber,
                        completed = completed,
                        total = items.size,
                        failed = failed,
                        title = item.titleAr
                    )
                )
            }

            return Result.success(
                buildProgressData(
                    unitNumber = unitNumber,
                    completed = completed,
                    total = items.size,
                    failed = failed,
                    title = "اكتمل تنزيل الأصوات بنجاح!"
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AudioDownloadWorker error", e)
            return Result.failure(
                buildProgressData(
                    unitNumber = unitNumber,
                    completed = completed,
                    total = items.size,
                    failed = failed + 1,
                    title = e.localizedMessage ?: "حدث خطأ أثناء التنزيل"
                )
            )
        } finally {
            audioManager.release()
            tempWordSpeaker.shutdown()
        }
    }

    private fun buildProgressData(
        unitNumber: Int,
        completed: Int,
        total: Int,
        failed: Int,
        title: String
    ): Data = workDataOf(
        KEY_UNIT_NUMBER to unitNumber,
        KEY_COMPLETED to completed,
        KEY_TOTAL to total,
        KEY_FAILED to failed,
        KEY_CURRENT_TITLE to title
    )

    companion object {
        private const val TAG = "AudioDownloadWorker"
        const val KEY_UNIT_NUMBER = "unit_number"
        const val KEY_COMPLETED = "completed"
        const val KEY_TOTAL = "total"
        const val KEY_FAILED = "failed"
        const val KEY_CURRENT_TITLE = "current_title"
        private const val UNIQUE_WORK_NAME = "hybrid_audio_pack_download"

        fun enqueueDownload(context: Context, unitNumber: Int) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<AudioDownloadWorker>()
                .setConstraints(constraints)
                .setInputData(workDataOf(KEY_UNIT_NUMBER to unitNumber))
                .build()

            WorkManager.getInstance(context.applicationContext)
                .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }

        fun pauseDownload(context: Context) {
            WorkManager.getInstance(context.applicationContext)
                .cancelUniqueWork(UNIQUE_WORK_NAME)
        }

        fun observeProgress(context: Context): Flow<AudioDownloadProgress?> {
            return WorkManager.getInstance(context.applicationContext)
                .getWorkInfosForUniqueWorkFlow(UNIQUE_WORK_NAME)
                .map { infos ->
                    val info = infos.firstOrNull() ?: return@map null
                    val data = when (info.state) {
                        WorkInfo.State.RUNNING -> info.progress
                        WorkInfo.State.SUCCEEDED, WorkInfo.State.FAILED -> info.outputData
                        else -> info.progress
                    }
                    val unitNum = data.getInt(KEY_UNIT_NUMBER, -1)
                    val completed = data.getInt(KEY_COMPLETED, 0)
                    val total = data.getInt(KEY_TOTAL, 0)
                    val failed = data.getInt(KEY_FAILED, 0)
                    val title = data.getString(KEY_CURRENT_TITLE).orEmpty()

                    val statusMsg = when (info.state) {
                        WorkInfo.State.ENQUEUED -> "بانتظار الاتصال بالإنترنت لبدء التنزيل..."
                        WorkInfo.State.RUNNING -> "جاري التنزيل في الخلفية ($completed / $total)..."
                        WorkInfo.State.SUCCEEDED -> "تم تنزيل $completed ملف صوتي وحفظها للاستخدام دون إنترنت ✓"
                        WorkInfo.State.CANCELLED -> "تم إيقاف التنزيل مؤقتاً — يمكنك الاستئناف في أي وقت دون تكرار."
                        WorkInfo.State.FAILED -> title.ifBlank { "توقف التنزيل بسبب مشكلة في الاتصال أو المصادقة." }
                        WorkInfo.State.BLOCKED -> "بانتظار توفر الشبكة..."
                    }

                    AudioDownloadProgress(
                        unitNumber = unitNum,
                        isRunning = info.state == WorkInfo.State.RUNNING || info.state == WorkInfo.State.ENQUEUED,
                        completedCount = completed,
                        totalCount = total,
                        failedCount = failed,
                        currentTitleAr = title,
                        statusMessageAr = statusMsg
                    )
                }
        }
    }
}
