package site.jagged.planneriti.data.remote.api

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GradesApi {
    @POST("date/login")
    @FormUrlEncoded
    suspend fun login(@Field("idnp") idnp: String): ResponseBody
}