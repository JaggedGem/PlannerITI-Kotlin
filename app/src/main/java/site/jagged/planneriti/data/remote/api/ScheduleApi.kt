package site.jagged.planneriti.data.remote.api

import site.jagged.planneriti.data.remote.dto.GroupDto
import site.jagged.planneriti.data.remote.dto.ScheduleResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {
    @GET("v1/grupe")
    suspend fun getGroups(): List<GroupDto>

    @GET("v1/orar")
    suspend fun getSchedule(
        @Query("_id") groupId: String,
        @Query("tip") type: String = "class"
    ): ScheduleResponseDto
}