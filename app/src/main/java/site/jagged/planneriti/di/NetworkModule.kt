package site.jagged.planneriti.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import site.jagged.planneriti.data.remote.api.GradesApi
import site.jagged.planneriti.data.remote.api.PapiApi
import site.jagged.planneriti.data.remote.api.ScheduleApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideScheduleApi(okHttpClient: OkHttpClient): ScheduleApi {
        return Retrofit.Builder()
            .baseUrl("https://orar-api.ceiti.md/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScheduleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGradesApi(okHttpClient: OkHttpClient): GradesApi {
        return Retrofit.Builder()
            .baseUrl("https://api.ceiti.md/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GradesApi::class.java)
    }

    @Provides
    @Singleton
    fun providePapiApi(okHttpClient: OkHttpClient): PapiApi {
        return Retrofit.Builder()
            .baseUrl("https://papi.jagged.site/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PapiApi::class.java)
    }
}