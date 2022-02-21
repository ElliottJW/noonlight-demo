package com.noonlight.apps.domain.environment

import com.noonlight.apps.BuildConfig
import java.net.URL

object NoonlightDevEnvironment : NoonlightEnvironment {
    override val baseUrl: URL
        get() = URL(BuildConfig.NOONLIGHT_SANDBOX_BASE_URL)
    override val apiToken: String
        get() = BuildConfig.NOONLIGHT_SANDBOX_API_TOKEN
}