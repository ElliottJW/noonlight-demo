package com.noonlight.apps.di

import android.content.Context
import com.noonlight.apps.domain.location.LocationRepository
import com.noonlight.apps.domain.location.LocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class NoonlightAppModule {

    @Provides
    fun provideLocationRepository(
        @ApplicationContext context: Context
    ) : LocationRepository {
        return LocationRepositoryImpl(
            context = context
        )
    }
}