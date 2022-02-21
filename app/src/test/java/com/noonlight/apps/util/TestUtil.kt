package com.noonlight.apps.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

object TestUtil {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()

    fun <T> getTestObjectFromFile(filePath: String, typeOfT: Class<T>): T {
        val rawString: String = this.javaClass.classLoader.getResource(filePath).readText()
        return moshi.adapter(typeOfT).fromJson(rawString)!!
    }
}