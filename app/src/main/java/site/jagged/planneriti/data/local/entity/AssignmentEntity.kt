package site.jagged.planneriti.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val courseCode: String,
    val courseName: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val isPriority: Boolean,
    val assignmentType: String,
    val periodId: String?,
    val subjectId: String?,
    val isOrphaned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)