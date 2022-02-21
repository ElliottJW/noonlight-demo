package com.noonlight.apps.domain.environment

import com.noonlight.apps.BuildConfig
import java.net.URL

object NoonlightProdEnvironment : NoonlightEnvironment {
    override val baseUrl: URL
        get() = URL(BuildConfig.NOONLIGHT_PROD_BASE_URL)
    override val apiToken: String
        get() = BuildConfig.NOONLIGHT_PROD_API_TOKEN
}