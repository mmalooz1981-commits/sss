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

@Database(
    entities = [
        ChildProfileEntity::class,
        UnitProgressEntity::class,
        ExamResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LinaDatabase : RoomDatabase() {
    abstract fun progressDao(): LinaProgressDao

    companion object {
        @Volatile
        private var INSTANCE: LinaDatabase? = null

        fun getInstance(context: Context): LinaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LinaDatabase::class.java,
                    "teacher_lina_offline.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

class LinaLocalRepository(private val dao: LinaProgressDao) {

    val profileFlow: Flow<ChildProfileEntity?> = dao.observeProfile()
    val unitProgressFlow: Flow<List<UnitProgressEntity>> = dao.observeUnitProgress()
    val examResultsFlow: Flow<List<ExamResultEntity>> = dao.observeExamResults()

    suspend fun ensureInitialized() {
        if (dao.getProfileOnce() == null) {
            dao.upsertProfile(ChildProfileEntity())
        }
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
