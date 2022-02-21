package com.noonlight.apps.network.client

import com.noonlight.apps.domain.environment.NoonlightEnvironment
import com.noonlight.apps.network.api.NoonlightApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URL
import java.util.*

class NoonlightNetworkClient(
    override val environment: NoonlightEnvironment
) : NetworkClient {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            var request = it.request()
            if (request.header("Authorization").isNullOrBlank()) {
                val bearerToken = "Bearer ${environment.apiToken}"
                request = request.newBuilder()
                    .addHeader("Authorization", bearerToken)
                    .build()
            }
            it.proceed(request)
        }
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()

    /**
     * Retrofit client instance to make network calls.
     * Add MoshiConverterFactory to translate JSON into Kotlin models.
     *
     * TODO: Add custom OkHttp interceptors to determine network status and response.
     */
    private val _retrofitClient: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(environment.baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    override fun getApi(): NoonlightApi {
        return _retrofitClient.create(NoonlightApi::class.java)
    }
}