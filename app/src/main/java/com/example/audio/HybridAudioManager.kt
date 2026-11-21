package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.local.CachedAudioDao
import com.example.data.local.CachedAudioEntity
import com.example.data.local.VoiceSettingsDao
import com.example.data.local.VoiceSettingsEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Central Hybrid AI English Audio Manager:
 * - Resolves local verified Room + internal storage audio first (instant offline playback via Media3 ExoPlayer).
 * - When missing and online, requests synthesis from secure Cloud Function (Chirp 3 HD) or Firebase AI Logic (Gemini TTS),
 *   persists the file locally with SHA-256 integrity metadata, and plays it immediately.
 * - Handles offline state with clear user feedback and optional device TTS fallback.
 * - Supports Slow / Normal / Practice (2-second repeat) modes, multi-speaker dialogues, and student voice recording.
 */
class HybridAudioManager(
    context: Context,
    private val cachedAudioDao: CachedAudioDao,
    private val voiceSettingsDao: VoiceSettingsDao,
    private val wordSpeaker: WordSpeaker,
    private val synthesizer: CloudVoiceSynthesizer = CloudVoiceSynthesizer()
) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val audioRootDir: File = File(appContext.filesDir, "hybrid_audio").apply { mkdirs() }
    private val studentRecordingFile: File = File(appContext.cacheDir, "student_voice_practice.m4a")

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(appContext).build()
    private var mediaRecorder: MediaRecorder? = null
    private var playbackJob: Job? = null
    private var currentPlaybackCompletion: CompletableDeferred<Unit>? = null

    private val _playbackState = MutableStateFlow(
        AudioPlaybackUiState(hasStudentRecording = studentRecordingFile.exists() && studentRecordingFile.length() > 0L)
    )
    val playbackState: StateFlow<AudioPlaybackUiState> = _playbackState.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                    currentPlaybackCompletion?.complete(Unit)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "ExoPlayer error: ${error.message}", error)
                currentPlaybackCompletion?.complete(Unit)
            }
        })
    }

    fun isOnline(): Boolean {
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun clearNotice() {
        _playbackState.update { it.copy(noticeMessageAr = null, isOfflineMissingNotice = false) }
    }

    /**
     * Plays an English word, sentence, grammar rule, story, or exam prompt using the 6-step hybrid flow.
     */
    fun playEnglish(
        textEn: String,
        unitNumber: Int = 0,
        lessonNumber: Int = 0,
        category: AudioContentCategory = AudioContentCategory.WORD,
        personaOverride: VoicePersona? = null,
        preferExpressiveGemini: Boolean = false,
        forcePracticeSequence: Boolean = false
    ) {
        val cleanText = textEn.trim()
        if (cleanText.isEmpty()) return

        stopAllPlayback()
        playbackJob = scope.launch {
            val settings = withContext(Dispatchers.IO) {
                voiceSettingsDao.getSettingsOnce() ?: VoiceSettingsEntity()
            }
            val accent = VoiceAccent.fromCode(settings.accentCode)
            val persona = personaOverride ?: VoicePersona.fromId(settings.personaId)
            val mode = if (forcePracticeSequence) SpeechMode.PRACTICE else SpeechMode.fromId(settings.speechModeId)
            val enginePref = VoiceEnginePreference.fromId(settings.enginePreferenceId)

            val shouldRepeat = mode == SpeechMode.PRACTICE
            playSingleSegment(
                textEn = cleanText,
                unitNumber = unitNumber,
                lessonNumber = lessonNumber,
                category = category,
                accent = accent,
                persona = persona,
                speechMode = mode,
                enginePref = enginePref,
                preferExpressiveGemini = preferExpressiveGemini,
                endpointUrl = settings.cloudTtsEndpointUrl,
                phaseLabelAr = if (shouldRepeat) "الاستماع الأول — استمع جيداً 👂" else null
            )

            if (shouldRepeat) {
                _playbackState.update {
                    it.copy(
                        isPlaying = false,
                        practicePhaseLabelAr = "دورك الآن! ردّد الكلمة خلال ثانيتين... 🗣️"
                    )
                }
                delay(2000L)
                playSingleSegment(
                    textEn = cleanText,
                    unitNumber = unitNumber,
                    lessonNumber = lessonNumber,
                    category = category,
                    accent = accent,
                    persona = persona,
                    speechMode = mode,
                    enginePref = enginePref,
                    preferExpressiveGemini = preferExpressiveGemini,
                    endpointUrl = settings.cloudTtsEndpointUrl,
                    phaseLabelAr = "الإعادة للتثبيت 🔁"
                )
            }

            _playbackState.update {
                it.copy(
                    isPlaying = false,
                    isLoadingCloud = false,
                    practicePhaseLabelAr = if (shouldRepeat) "أحسنت! اضغط للاستماع مرة أخرى 🌟" else null
                )
            }
        }
    }

    /**
     * Plays a complete multi-speaker dialogue preserving turn order, distinct voices, and natural pauses.
     */
    fun playFullDialogue(script: DialogueScript) {
        if (script.turns.isEmpty()) return
        stopAllPlayback()
        playbackJob = scope.launch {
            val settings = withContext(Dispatchers.IO) {
                voiceSettingsDao.getSettingsOnce() ?: VoiceSettingsEntity()
            }
            val accent = VoiceAccent.fromCode(settings.accentCode)
            val mode = SpeechMode.fromId(settings.speechModeId)
            val enginePref = VoiceEnginePreference.fromId(settings.enginePreferenceId)

            script.turns.forEachIndexed { index, turn ->
                _playbackState.update {
                    it.copy(
                        activeDialogueId = script.id,
                        activeDialogueTurnIndex = index
                    )
                }
                playSingleSegment(
                    textEn = turn.textEn,
                    unitNumber = script.unitNumber,
                    lessonNumber = 3,
                    category = AudioContentCategory.DIALOGUE,
                    accent = accent,
                    persona = turn.persona,
                    speechMode = mode,
                    enginePref = enginePref,
                    preferExpressiveGemini = true,
                    endpointUrl = settings.cloudTtsEndpointUrl,
                    phaseLabelAr = "${turn.persona.emoji} ${turn.speakerName} يتحدث الآن..."
                )
                if (index < script.turns.lastIndex) {
                    delay(650L) // Natural conversational pause between speakers
                }
            }

            _playbackState.update {
                it.copy(
                    isPlaying = false,
                    isLoadingCloud = false,
                    activeDialogueId = null,
                    activeDialogueTurnIndex = -1,
                    practicePhaseLabelAr = null
                )
            }
        }
    }

    /**
     * Plays a single turn of a dialogue with its character's assigned voice.
     */
    fun playDialogueTurn(script: DialogueScript, turnIndex: Int) {
        val turn = script.turns.getOrNull(turnIndex) ?: return
        stopAllPlayback()
        playbackJob = scope.launch {
            val settings = withContext(Dispatchers.IO) {
                voiceSettingsDao.getSettingsOnce() ?: VoiceSettingsEntity()
            }
            _playbackState.update {
                it.copy(
                    activeDialogueId = script.id,
                    activeDialogueTurnIndex = turnIndex
                )
            }
            playSingleSegment(
                textEn = turn.textEn,
                unitNumber = script.unitNumber,
                lessonNumber = 3,
                category = AudioContentCategory.DIALOGUE,
                accent = VoiceAccent.fromCode(settings.accentCode),
                persona = turn.persona,
                speechMode = SpeechMode.fromId(settings.speechModeId),
                enginePref = VoiceEnginePreference.fromId(settings.enginePreferenceId),
                preferExpressiveGemini = true,
                endpointUrl = settings.cloudTtsEndpointUrl,
                phaseLabelAr = "${turn.persona.emoji} ${turn.speakerName}"
            )
            _playbackState.update {
                it.copy(
                    isPlaying = false,
                    isLoadingCloud = false,
                    activeDialogueId = null,
                    activeDialogueTurnIndex = -1,
                    practicePhaseLabelAr = null
                )
            }
        }
    }

    fun playArabic(textAr: String) {
        stopAllPlayback()
        wordSpeaker.speakArabic(textAr)
    }

    fun speakLocalFallbackNow(textEn: String) {
        stopAllPlayback()
        playbackJob = scope.launch {
            val settings = withContext(Dispatchers.IO) {
                voiceSettingsDao.getSettingsOnce() ?: VoiceSettingsEntity()
            }
            val accent = VoiceAccent.fromCode(settings.accentCode)
            val persona = VoicePersona.fromId(settings.personaId)
            val mode = SpeechMode.fromId(settings.speechModeId)
            playViaDeviceTtsAndAwait(textEn, accent, persona, mode)
            _playbackState.update { it.copy(isPlaying = false, isOfflineMissingNotice = false) }
        }
    }

    private suspend fun playSingleSegment(
        textEn: String,
        unitNumber: Int,
        lessonNumber: Int,
        category: AudioContentCategory,
        accent: VoiceAccent,
        persona: VoicePersona,
        speechMode: SpeechMode,
        enginePref: VoiceEnginePreference,
        preferExpressiveGemini: Boolean,
        endpointUrl: String,
        phaseLabelAr: String?
    ) {
        // Step 1: Check local Room cache & verify integrity on disk
        val localFile = findVerifiedLocalAudio(
            textEn = textEn,
            accent = accent,
            persona = persona,
            enginePref = enginePref,
            preferExpressiveGemini = preferExpressiveGemini,
            endpointUrl = endpointUrl
        )

        if (localFile != null) {
            // Step 2: Play directly offline via Media3 ExoPlayer
            _playbackState.update {
                it.copy(
                    isPlaying = true,
                    isLoadingCloud = false,
                    activeText = textEn,
                    activeSourceLabelAr = "محلياً (دون إنترنت) • ${persona.displayNameEn} (${accent.code})",
                    practicePhaseLabelAr = phaseLabelAr,
                    noticeMessageAr = null,
                    isOfflineMissingNotice = false
                )
            }
            playLocalFileWithExoPlayerAndAwait(localFile.first, speechMode.playbackSpeed)
            return
        }

        // Step 3: Not cached — check internet availability
        if (!isOnline()) {
            // Step 6: Clear offline message when neither local cache nor internet is available
            _playbackState.update {
                it.copy(
                    isPlaying = false,
                    isLoadingCloud = false,
                    activeText = textEn,
                    activeSourceLabelAr = "غير متصل بالإنترنت",
                    noticeMessageAr = "لا يوجد اتصال بالإنترنت ولم يتم تنزيل هذا الصوت مسبقاً. يمكنك تنزيل أصوات الوحدة من «مكتبة الأصوات» عند توفر الإنترنت، أو استخدام صوت الجهاز الاحتياطي.",
                    isOfflineMissingNotice = true
                )
            }
            // Still provide non-silent local device TTS fallback so the child isn't blocked mid-lesson
            playViaDeviceTtsAndAwait(textEn, accent, persona, speechMode)
            return
        }

        // Step 3 & 4: Request synthesis from secure backend / Firebase AI Logic and cache locally
        _playbackState.update {
            it.copy(
                isLoadingCloud = true,
                isPlaying = false,
                activeText = textEn,
                activeSourceLabelAr = "جاري توليد الصوت الذكي...",
                practicePhaseLabelAr = phaseLabelAr,
                noticeMessageAr = null,
                isOfflineMissingNotice = false
            )
        }

        val cachedEntry = ensureItemCached(
            item = CurriculumAudioItem(
                unitNumber = unitNumber,
                lessonNumber = lessonNumber,
                category = category,
                titleAr = textEn,
                textEn = textEn,
                preferredPersona = persona,
                preferExpressiveGemini = preferExpressiveGemini
            ),
            accent = accent,
            personaOverride = persona,
            speechMode = speechMode,
            enginePref = enginePref,
            endpointUrl = endpointUrl
        )

        when (cachedEntry) {
            is CacheItemOutcome.Ready -> {
                // Step 5: Play newly saved file via Media3 ExoPlayer
                _playbackState.update {
                    it.copy(
                        isLoadingCloud = false,
                        isPlaying = true,
                        activeSourceLabelAr = "${cachedEntry.entry.engineUsed} • محفوظ محلياً",
                        noticeMessageAr = null,
                        isOfflineMissingNotice = false
                    )
                }
                playLocalFileWithExoPlayerAndAwait(cachedEntry.file, speechMode.playbackSpeed)
            }
            is CacheItemOutcome.Failed -> {
                _playbackState.update {
                    it.copy(
                        isLoadingCloud = false,
                        isPlaying = true,
                        activeSourceLabelAr = "صوت الجهاز الاحتياطي (${accent.code})",
                        noticeMessageAr = cachedEntry.reasonAr,
                        isOfflineMissingNotice = false
                    )
                }
                playViaDeviceTtsAndAwait(textEn, accent, persona, speechMode)
            }
        }
    }

    /**
     * Checks local cache (preventing duplicate downloads), or synthesizes & saves a single item.
     * Used both by live hybrid playback and by WorkManager background unit downloads.
     */
    suspend fun ensureItemCached(
        item: CurriculumAudioItem,
        accent: VoiceAccent,
        personaOverride: VoicePersona? = null,
        speechMode: SpeechMode = SpeechMode.NORMAL,
        enginePref: VoiceEnginePreference = VoiceEnginePreference.HYBRID_AUTO,
        endpointUrl: String = ""
    ): CacheItemOutcome = withContext(Dispatchers.IO) {
        val persona = personaOverride ?: item.preferredPersona ?: VoicePersona.MISS_LILY
        val engineTag = resolveEngineTag(enginePref, item.preferExpressiveGemini, endpointUrl)
        val cacheKey = AudioKeyHasher.buildCacheKey(item.textEn, accent, persona, engineTag)

        // Prevent duplicate download if already cached and verified
        val existing = cachedAudioDao.getByCacheKey(cacheKey)
        if (existing != null) {
            val file = File(audioRootDir, existing.relativeFilePath)
            if (verifyFileIntegrity(file, existing.sha256)) {
                return@withContext CacheItemOutcome.Ready(existing, file, wasAlreadyCached = true)
            } else {
                // Corrupted or missing file — clean stale record before re-downloading
                file.delete()
                cachedAudioDao.deleteByCacheKey(cacheKey)
            }
        }

        val synth = synthesizer.synthesize(
            textEn = item.textEn,
            accent = accent,
            persona = persona,
            speechMode = speechMode,
            enginePreference = enginePref,
            preferExpressiveGemini = item.preferExpressiveGemini,
            cloudTtsEndpointUrl = endpointUrl
        )

        when (synth) {
            is SynthesisResult.Failure -> CacheItemOutcome.Failed(synth.reasonAr)
            is SynthesisResult.Success -> {
                val bytes = synth.audio.bytes
                if (bytes.isEmpty()) {
                    return@withContext CacheItemOutcome.Failed("ملف الصوت المستلم فارغ.")
                }
                val unitDir = "unit_${item.unitNumber}/lesson_${item.lessonNumber}"
                val targetDir = File(audioRootDir, unitDir).apply { mkdirs() }
                val fileName = "${cacheKey}.${synth.audio.fileExtension}"
                val targetFile = File(targetDir, fileName)
                val tempFile = File(targetDir, "$fileName.tmp")

                tempFile.writeBytes(bytes)
                val sha = AudioKeyHasher.sha256Hex(bytes)
                if (!tempFile.renameTo(targetFile)) {
                    targetFile.writeBytes(bytes)
                    tempFile.delete()
                }

                val entity = CachedAudioEntity(
                    cacheKey = cacheKey,
                    unitNumber = item.unitNumber,
                    lessonNumber = item.lessonNumber,
                    category = item.category.id,
                    textEn = item.textEn,
                    accentCode = accent.code,
                    personaId = persona.id,
                    engineUsed = synth.audio.engineUsed,
                    relativeFilePath = "$unitDir/$fileName",
                    fileSizeBytes = targetFile.length(),
                    sha256 = sha,
                    mimeType = synth.audio.mimeType,
                    downloadedAtMillis = System.currentTimeMillis()
                )
                cachedAudioDao.upsert(entity)
                CacheItemOutcome.Ready(entity, targetFile, wasAlreadyCached = false)
            }
        }
    }

    private suspend fun findVerifiedLocalAudio(
        textEn: String,
        accent: VoiceAccent,
        persona: VoicePersona,
        enginePref: VoiceEnginePreference,
        preferExpressiveGemini: Boolean,
        endpointUrl: String
    ): Pair<File, CachedAudioEntity>? = withContext(Dispatchers.IO) {
        val primaryTag = resolveEngineTag(enginePref, preferExpressiveGemini, endpointUrl)
        val candidateKeys = listOf(
            AudioKeyHasher.buildCacheKey(textEn, accent, persona, primaryTag),
            AudioKeyHasher.buildCacheKey(textEn, accent, persona, "chirp3"),
            AudioKeyHasher.buildCacheKey(textEn, accent, persona, "gemini")
        ).distinct()

        for (key in candidateKeys) {
            val entry = cachedAudioDao.getByCacheKey(key) ?: continue
            val file = File(audioRootDir, entry.relativeFilePath)
            if (verifyFileIntegrity(file, entry.sha256)) {
                return@withContext file to entry
            } else {
                file.delete()
                cachedAudioDao.deleteByCacheKey(key)
            }
        }
        null
    }

    fun verifyFileIntegrity(file: File, expectedSha256: String): Boolean {
        if (!file.exists() || !file.isFile || file.length() <= 0L) return false
        return try {
            val actual = AudioKeyHasher.sha256Hex(file.readBytes())
            actual.equals(expectedSha256, ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    private fun resolveEngineTag(
        enginePref: VoiceEnginePreference,
        preferExpressiveGemini: Boolean,
        endpointUrl: String
    ): String {
        val hasBackend = endpointUrl.trim().startsWith("https://")
        return when (enginePref) {
            VoiceEnginePreference.GEMINI_TTS -> "gemini"
            VoiceEnginePreference.CHIRP3_HD -> if (hasBackend) "chirp3" else "gemini"
            VoiceEnginePreference.HYBRID_AUTO -> if (preferExpressiveGemini || !hasBackend) "gemini" else "chirp3"
        }
    }

    private suspend fun playLocalFileWithExoPlayerAndAwait(file: File, speed: Float) {
        val deferred = CompletableDeferred<Unit>()
        currentPlaybackCompletion = deferred
        try {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
            exoPlayer.playbackParameters = PlaybackParameters(speed.coerceIn(0.6f, 1.4f))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            deferred.await()
        } catch (e: CancellationException) {
            exoPlayer.stop()
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed playing local file with ExoPlayer", e)
        } finally {
            if (currentPlaybackCompletion === deferred) {
                currentPlaybackCompletion = null
            }
        }
    }

    private suspend fun playViaDeviceTtsAndAwait(
        textEn: String,
        accent: VoiceAccent,
        persona: VoicePersona,
        speechMode: SpeechMode
    ) {
        val deferred = CompletableDeferred<Unit>()
        try {
            wordSpeaker.speakEnglishWithProfile(
                text = textEn,
                accent = accent,
                persona = persona,
                speechMode = speechMode,
                onComplete = { deferred.complete(Unit) }
            )
            deferred.await()
        } catch (e: CancellationException) {
            wordSpeaker.stop()
            throw e
        }
    }

    // ==================== STUDENT VOICE RECORDING & SELF-COMPARISON ====================

    fun startStudentRecording(): Boolean {
        stopAllPlayback()
        stopStudentRecording()
        return try {
            if (studentRecordingFile.exists()) studentRecordingFile.delete()
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(appContext)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(24000)
                setAudioEncodingBitRate(64000)
                setOutputFile(studentRecordingFile.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            _playbackState.update {
                it.copy(
                    isRecordingStudent = true,
                    noticeMessageAr = "جاري تسجيل صوتك الآن... انطق الكلمة بوضوح ثم اضغط إيقاف 🎤"
                )
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Student MediaRecorder start failed", e)
            _playbackState.update {
                it.copy(
                    isRecordingStudent = false,
                    noticeMessageAr = "تعذّر تشغيل مسجل الصوت على هذا الجهاز."
                )
            }
            false
        }
    }

    fun stopStudentRecording() {
        val recorder = mediaRecorder ?: return
        try {
            recorder.stop()
        } catch (_: Exception) {
        } finally {
            try {
                recorder.release()
            } catch (_: Exception) {
            }
            mediaRecorder = null
            val hasFile = studentRecordingFile.exists() && studentRecordingFile.length() > 0L
            _playbackState.update {
                it.copy(
                    isRecordingStudent = false,
                    hasStudentRecording = hasFile,
                    noticeMessageAr = if (hasFile) {
                        "تم حفظ تسجيلك! استمع لصوتك وقارنه بنطق المعلمة (ملاحظة تعليمية: الاستماع والمقارنة المباشرة أفضل وسيلة لتحسين مخارج الحروف)."
                    } else null
                )
            }
        }
    }

    fun playStudentRecording() {
        if (!studentRecordingFile.exists() || studentRecordingFile.length() <= 0L) return
        stopAllPlayback()
        playbackJob = scope.launch {
            _playbackState.update {
                it.copy(
                    isPlayingStudentRecording = true,
                    isPlaying = true,
                    activeSourceLabelAr = "تسجيل صوت الطالب 🎤"
                )
            }
            playLocalFileWithExoPlayerAndAwait(studentRecordingFile, 1.0f)
            _playbackState.update {
                it.copy(
                    isPlayingStudentRecording = false,
                    isPlaying = false
                )
            }
        }
    }

    // ==================== STORAGE MANAGEMENT ====================

    suspend fun deleteUnitAudio(unitNumber: Int) = withContext(Dispatchers.IO) {
        val entries = cachedAudioDao.getByUnitOnce(unitNumber)
        entries.forEach { entry ->
            File(audioRootDir, entry.relativeFilePath).delete()
        }
        File(audioRootDir, "unit_$unitNumber").deleteRecursively()
        cachedAudioDao.deleteByUnit(unitNumber)
    }

    suspend fun deleteAllAudio() = withContext(Dispatchers.IO) {
        val entries = cachedAudioDao.getAllOnce()
        entries.forEach { entry ->
            File(audioRootDir, entry.relativeFilePath).delete()
        }
        audioRootDir.listFiles()?.forEach { it.deleteRecursively() }
        cachedAudioDao.deleteAll()
    }

    fun stopAllPlayback() {
        playbackJob?.cancel()
        playbackJob = null
        currentPlaybackCompletion?.complete(Unit)
        currentPlaybackCompletion = null
        try {
            exoPlayer.stop()
        } catch (_: Exception) {
        }
        wordSpeaker.stop()
        _playbackState.update {
            it.copy(
                isPlaying = false,
                isLoadingCloud = false,
                isPlayingStudentRecording = false,
                activeDialogueId = null,
                activeDialogueTurnIndex = -1,
                practicePhaseLabelAr = null
            )
        }
    }

    fun release() {
        stopStudentRecording()
        stopAllPlayback()
        scope.cancel()
        try {
            exoPlayer.release()
        } catch (_: Exception) {
        }
    }

    companion object {
        private const val TAG = "HybridAudioManager"
    }
}

sealed interface CacheItemOutcome {
    data class Ready(
        val entry: CachedAudioEntity,
        val file: File,
        val wasAlreadyCached: Boolean
    ) : CacheItemOutcome

    data class Failed(val reasonAr: String) : CacheItemOutcome
}
