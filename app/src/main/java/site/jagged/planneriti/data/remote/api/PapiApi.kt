package site.jagged.planneriti.data.remote.api

//import site.jagged.planneriti.data.remote.dto.RecoveryDayDto
import retrofit2.http.GET

data class RecoveryDayDto(
    val date: String,
    val replacedDay: String,
    val reason: String,
    val groupId: String,
    val isActive: Boolean
)

interface PapiApi {
    @GET("api/recovery-days")
    suspend fun getRecoveryDays(): List<RecoveryDayDto>
}