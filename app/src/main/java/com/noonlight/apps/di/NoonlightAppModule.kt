package com.noonlight.apps.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.noonlight.apps.BuildConfig
import com.noonlight.apps.domain.environment.NoonlightDevEnvironment
import com.noonlight.apps.domain.environment.NoonlightEnvironment
import com.noonlight.apps.domain.environment.NoonlightProdEnvironment
import com.noonlight.apps.domain.location.LocationRepository
import com.noonlight.apps.domain.location.LocationRepositoryImpl
import com.noonlight.apps.domain.user.DemoUserProvider
import com.noonlight.apps.domain.user.UserProvider
import com.noonlight.apps.network.api.NoonlightApi
import com.noonlight.apps.network.client.NetworkClient
import com.noonlight.apps.network.client.NoonlightNetworkClient
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

    /**
     * Notice: Providing the API separately here for simplicity. If you needed to
     * access other functions within the NetworkClient instead, that may be better
     * to access in your ViewModels than just the straight API.
     */
    @Provides
    fun provideNoonlightApi(
        networkClient: NetworkClient
    ): NoonlightApi {
        return networkClient.getApi()
    }

    @Provides
    fun provideNetworkClient(
        noonlightEnvironment: NoonlightEnvironment
    ) : NetworkClient {
        return NoonlightNetworkClient(noonlightEnvironment)
    }

    /**
     * Noonlight dev documentation references two environments - prod and sandbox.
     *
     * TODO: Check the PROD URL.
     */
    @Provides
    fun provideNoonlightEnvironment() : NoonlightEnvironment {
        // TODO: Add an EnvironmentProvider that could change
        //  the environment from setting within the app.
        return if (BuildConfig.DEBUG) {
            NoonlightDevEnvironment
        } else {
            NoonlightProdEnvironment
        }
    }

    @Provides
    fun provideUserProvider() : UserProvider = DemoUserProvider()
}