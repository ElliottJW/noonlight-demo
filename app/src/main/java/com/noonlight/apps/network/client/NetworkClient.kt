package com.noonlight.apps.network.client

import com.noonlight.apps.domain.environment.NoonlightEnvironment
import com.noonlight.apps.network.api.NoonlightApi

interface NetworkClient {
    val environment: NoonlightEnvironment
    fun getApi() : NoonlightApi
}