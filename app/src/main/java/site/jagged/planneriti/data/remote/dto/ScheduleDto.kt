package site.jagged.planneriti.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("_id") val id: String,
    val name: String,
    val diriginte: TeacherDto?
)

data class TeacherDto(val name: String)

data class ScheduleResponseDto(
    val data: Map<String, Map<String, PeriodSchedulesDto>>,
    val periods: List<PeriodDto>
)

data class PeriodDto(
    @SerializedName("_id") val id: String,
    val starttime: String,
    val endtime: String
)

data class PeriodSchedulesDto(
    val both: List<ScheduleItemDto> = emptyList(),
    val par: List<ScheduleItemDto> = emptyList(),
    val impar: List<ScheduleItemDto> = emptyList()
)

data class ScheduleItemDto(
    val subjectid: SubjectDto,
    val teacherids: TeacherDto,
    val classroomids: ClassroomDto,
    val groupids: GroupInfoDto,
    val cards: CardDto
)

data class SubjectDto(val name: String)
data class ClassroomDto(val name: String)
data class GroupInfoDto(val name: String, val entireclass: String?)
data class CardDto(val period: String, val weeks: String?, val days: String?)