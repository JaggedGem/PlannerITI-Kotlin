package site.jagged.planneriti.data.local.entity

import androidx.room.*

@Entity(
    tableName = "subtasks",
    foreignKeys = [ForeignKey(
        entity = AssignmentEntity::class,
        parentColumns = ["id"],
        childColumns = ["assignmentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("assignmentId")]
)
data class SubtaskEntity(
    @PrimaryKey val id: String,
    val assignmentId: String,
    val title: String,
    val isCompleted: Boolean
)