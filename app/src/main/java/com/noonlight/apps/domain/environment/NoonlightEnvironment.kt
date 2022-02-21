package com.noonlight.apps.domain.environment

import java.net.URL

interface NoonlightEnvironment {
    val baseUrl: URL
    val apiToken: String
}