package site.jagged.planneriti.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import site.jagged.planneriti.data.local.dao.AssignmentDao
import site.jagged.planneriti.data.local.entity.AssignmentEntity
import site.jagged.planneriti.data.local.entity.SubtaskEntity

@Database(
    entities = [AssignmentEntity::class, SubtaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao
}