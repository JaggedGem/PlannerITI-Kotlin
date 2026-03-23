package site.jagged.planneriti.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import site.jagged.planneriti.data.local.SettingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule
// SettingsDataStore uses @Inject constructor so Hilt handles it automatically
// This module exists for any manual provides you add later