package com.example.tubarriolimpioapp.data.network

import com.example.tubarriolimpioapp.BuildConfig

object ApiConfig {
    val BASE_URL: String = BuildConfig.API_BASE_URL
    val BASE_ORIGIN: String = BASE_URL.removeSuffix("/api/").removeSuffix("/")
}
