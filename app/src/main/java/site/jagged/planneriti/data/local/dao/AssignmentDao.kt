package site.jagged.planneriti.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import site.jagged.planneriti.data.local.entity.AssignmentEntity
import site.jagged.planneriti.data.local.entity.SubtaskEntity

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC")
    fun getAll(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE isCompleted = 0 AND dueDate >= :now ORDER BY dueDate ASC")
    fun getActive(now: Long = System.currentTimeMillis()): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE dueDate < :now AND isCompleted = 0 ORDER BY dueDate DESC")
    fun getOverdue(now: Long = System.currentTimeMillis()): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE isPriority = 1 AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getPriority(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE id = :id")
    suspend fun getById(id: String): AssignmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assignment: AssignmentEntity)

    @Update
    suspend fun update(assignment: AssignmentEntity)

    @Query("DELETE FROM assignments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE assignments SET isCompleted = CASE WHEN isCompleted = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleCompletion(id: String)

    @Query("SELECT * FROM subtasks WHERE assignmentId = :assignmentId")
    fun getSubtasks(assignmentId: String): Flow<List<SubtaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity)

    @Query("DELETE FROM subtasks WHERE id = :id")
    suspend fun deleteSubtask(id: String)

    @Query("UPDATE subtasks SET isCompleted = CASE WHEN isCompleted = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleSubtask(id: String)
}