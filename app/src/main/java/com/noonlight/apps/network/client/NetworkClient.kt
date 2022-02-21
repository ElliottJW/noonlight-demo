package com.noonlight.apps.network.client

import com.noonlight.apps.network.api.NoonlightApi
import java.net.URL

interface NetworkClient {
    val baseUrl: URL
    fun getApi() : NoonlightApi
}