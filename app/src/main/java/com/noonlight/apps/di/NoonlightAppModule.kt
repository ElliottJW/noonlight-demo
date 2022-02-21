package com.noonlight.apps.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.noonlight.apps.BuildConfig
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
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideLocationRepository(
        @ApplicationContext context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient
    ) : LocationRepository {
        return LocationRepositoryImpl(
            context = context,
            fusedLocationProviderClient = fusedLocationProviderClient
        )
    }
}