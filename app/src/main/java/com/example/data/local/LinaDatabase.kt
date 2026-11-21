package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "child_profile")
data class ChildProfileEntity(
    @PrimaryKey val id: Int = 1,
    val childName: String = "بطل الصف الخامس",
    val totalStars: Int = 0,
    val selectedLevel: Int = 1,
    val selectedCompanionId: String = "lina",
    val wordsSpokenCount: Int = 0
)

@Entity(tableName = "unit_progress")
data class UnitProgressEntity(
    @PrimaryKey val unitNumber: Int,
    val level1Completed: Boolean = false,
    val level2Completed: Boolean = false,
    val level3Completed: Boolean = false,
    val bestScorePercent: Int = 0,
    val starsEarned: Int = 0,
    val attemptsCount: Int = 0
) {
    val hasMedal: Boolean
        get() = level1Completed || level2Completed || level3Completed
}

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examTitleAr: String,
    val unitNumber: Int, // 0 = Comprehensive Book Exam
    val correctAnswers: Int,
    val totalQuestions: Int,
    val starsEarned: Int,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_audio")
data class CachedAudioEntity(
    @PrimaryKey val cacheKey: String,
    val unitNumber: Int,
    val lessonNumber: Int,
    val category: String,
    val textEn: String,
    val accentCode: String,
    val personaId: String,
    val engineUsed: String,
    val relativeFilePath: String,
    val fileSizeBytes: Long,
    val sha256: String,
    val mimeType: String = "audio/mpeg",
    val downloadedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "voice_settings")
data class VoiceSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val accentCode: String = "en-GB",
    val personaId: String = "miss_lily",
    val speechModeId: String = "normal",
    val enginePreferenceId: String = "hybrid_auto",
    val cloudTtsEndpointUrl: String = ""
)

@Dao
interface LinaProgressDao {

    @Query("SELECT * FROM child_profile WHERE id = 1")
    fun observeProfile(): Flow<ChildProfileEntity?>

    @Query("SELECT * FROM child_profile WHERE id = 1")
    suspend fun getProfileOnce(): ChildProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: ChildProfileEntity)

    @Query("SELECT * FROM unit_progress ORDER BY unitNumber ASC")
    fun observeUnitProgress(): Flow<List<UnitProgressEntity>>

    @Query("SELECT * FROM unit_progress WHERE unitNumber = :unitNumber")
    suspend fun getUnitProgressOnce(unitNumber: Int): UnitProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUnitProgress(progress: UnitProgressEntity)

    @Query("SELECT * FROM exam_results ORDER BY timestampMillis DESC LIMIT 30")
    fun observeExamResults(): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultEntity)

    @Query("DELETE FROM unit_progress")
    suspend fun clearUnitProgress()

    @Query("DELETE FROM exam_results")
    suspend fun clearExamResults()
}

@Dao
interface CachedAudioDao {

    @Query("SELECT * FROM cached_audio")
    fun observeAllAudio(): Flow<List<CachedAudioEntity>>

    @Query("SELECT * FROM cached_audio WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getByCacheKey(cacheKey: String): CachedAudioEntity?

    @Query("SELECT * FROM cached_audio WHERE unitNumber = :unitNumber")
    suspend fun getByUnitOnce(unitNumber: Int): List<CachedAudioEntity>

    @Query("SELECT * FROM cached_audio")
    suspend fun getAllOnce(): List<CachedAudioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: CachedAudioEntity)

    @Query("DELETE FROM cached_audio WHERE cacheKey = :cacheKey")
    suspend fun deleteByCacheKey(cacheKey: String)

    @Query("DELETE FROM cached_audio WHERE unitNumber = :unitNumber")
    suspend fun deleteByUnit(unitNumber: Int)

    @Query("DELETE FROM cached_audio")
    suspend fun deleteAll()
}

@Dao
interface VoiceSettingsDao {

    @Query("SELECT * FROM voice_settings WHERE id = 1")
    fun observeSettings(): Flow<VoiceSettingsEntity?>

    @Query("SELECT * FROM voice_settings WHERE id = 1")
    suspend fun getSettingsOnce(): VoiceSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: VoiceSettingsEntity)
}

@Database(
    entities = [
        ChildProfileEntity::class,
        UnitProgressEntity::class,
        ExamResultEntity::class,
        CachedAudioEntity::class,
        VoiceSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LinaDatabase : RoomDatabase() {
    abstract fun progressDao(): LinaProgressDao
    abstract fun cachedAudioDao(): CachedAudioDao
    abstract fun voiceSettingsDao(): VoiceSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: LinaDatabase? = null

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `cached_audio` (
                        `cacheKey` TEXT NOT NULL,
                        `unitNumber` INTEGER NOT NULL,
                        `lessonNumber` INTEGER NOT NULL,
                        `category` TEXT NOT NULL,
                        `textEn` TEXT NOT NULL,
                        `accentCode` TEXT NOT NULL,
                        `personaId` TEXT NOT NULL,
                        `engineUsed` TEXT NOT NULL,
                        `relativeFilePath` TEXT NOT NULL,
                        `fileSizeBytes` INTEGER NOT NULL,
                        `sha256` TEXT NOT NULL,
                        `mimeType` TEXT NOT NULL,
                        `downloadedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`cacheKey`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `voice_settings` (
                        `id` INTEGER NOT NULL,
                        `accentCode` TEXT NOT NULL,
                        `personaId` TEXT NOT NULL,
                        `speechModeId` TEXT NOT NULL,
                        `enginePreferenceId` TEXT NOT NULL,
                        `cloudTtsEndpointUrl` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): LinaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LinaDatabase::class.java,
                    "teacher_lina_offline.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

class LinaLocalRepository(
    private val dao: LinaProgressDao,
    private val voiceSettingsDao: VoiceSettingsDao? = null
) {

    val profileFlow: Flow<ChildProfileEntity?> = dao.observeProfile()
    val unitProgressFlow: Flow<List<UnitProgressEntity>> = dao.observeUnitProgress()
    val examResultsFlow: Flow<List<ExamResultEntity>> = dao.observeExamResults()
    val voiceSettingsFlow: Flow<VoiceSettingsEntity?>? = voiceSettingsDao?.observeSettings()

    suspend fun ensureInitialized() {
        if (dao.getProfileOnce() == null) {
            dao.upsertProfile(ChildProfileEntity())
        }
        if (voiceSettingsDao != null && voiceSettingsDao.getSettingsOnce() == null) {
            voiceSettingsDao.upsert(VoiceSettingsEntity())
        }
    }

    suspend fun updateVoiceSettings(transform: (VoiceSettingsEntity) -> VoiceSettingsEntity) {
        val vDao = voiceSettingsDao ?: return
        val current = vDao.getSettingsOnce() ?: VoiceSettingsEntity()
        vDao.upsert(transform(current))
    }

    suspend fun updateSelectedLevel(levelId: Int) {
        val current = dao.getProfileOnce() ?: ChildProfileEntity()
        dao.upsertProfile(current.copy(selectedLevel = levelId))
    }

    suspend fun updateChildName(name: String) {
        val trimmed = name.trim().ifEmpty { "بطل الصف الخامس" }
        val current = dao.getProfileOnce() ?: ChildProfileEntity()
        dao.upsertProfile(current.copy(childName = trimmed))
    }

    suspend fun selectCompanion(companionId: String) {
        val current = dao.getProfileOnce() ?: ChildProfileEntity()
        dao.upsertProfile(current.copy(selectedCompanionId = companionId))
    }

    suspend fun recordSpokenPractice(bonusStars: Int) {
        val current = dao.getProfileOnce() ?: ChildProfileEntity()
        dao.upsertProfile(
            current.copy(
                totalStars = current.totalStars + bonusStars,
                wordsSpokenCount = current.wordsSpokenCount + 1
            )
        )
    }

    suspend fun recordGameCompletion(
        unitNumber: Int,
        levelId: Int,
        earnedStars: Int,
        correctCount: Int,
        totalQuestions: Int
    ) {
        val currentProfile = dao.getProfileOnce() ?: ChildProfileEntity()
        dao.upsertProfile(
            currentProfile.copy(totalStars = currentProfile.totalStars + earnedStars)
        )

        if (unitNumber in 1..8) {
            val percent = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
            val existing = dao.getUnitProgressOnce(unitNumber)
                ?: UnitProgressEntity(unitNumber = unitNumber)

            dao.upsertUnitProgress(
                existing.copy(
                    level1Completed = existing.level1Completed || levelId == 1,
                    level2Completed = existing.level2Completed || levelId == 2,
                    level3Completed = existing.level3Completed || levelId == 3,
                    bestScorePercent = maxOf(existing.bestScorePercent, percent),
                    starsEarned = existing.starsEarned + earnedStars,
                    attemptsCount = existing.attemptsCount + 1
                )
            )
        }
    }

    suspend fun recordExamResult(
        examTitleAr: String,
        unitNumber: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        starsEarned: Int
    ) {
        dao.insertExamResult(
            ExamResultEntity(
                examTitleAr = examTitleAr,
                unitNumber = unitNumber,
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                starsEarned = starsEarned
            )
        )
        val currentProfile = dao.getProfileOnce() ?: ChildProfileEntity()
        dao.upsertProfile(
            currentProfile.copy(totalStars = currentProfile.totalStars + starsEarned)
        )
        if (unitNumber in 1..8) {
            val percent = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
            val existing = dao.getUnitProgressOnce(unitNumber)
                ?: UnitProgressEntity(unitNumber = unitNumber)
            dao.upsertUnitProgress(
                existing.copy(
                    bestScorePercent = maxOf(existing.bestScorePercent, percent),
                    starsEarned = existing.starsEarned + starsEarned,
                    attemptsCount = existing.attemptsCount + 1,
                    level1Completed = existing.level1Completed || percent >= 50
                )
            )
        }
    }

    suspend fun resetAllProgress() {
        dao.clearUnitProgress()
        dao.clearExamResults()
        val currentName = dao.getProfileOnce()?.childName ?: "بطل الصف الخامس"
        dao.upsertProfile(ChildProfileEntity(childName = currentName))
    }
}
