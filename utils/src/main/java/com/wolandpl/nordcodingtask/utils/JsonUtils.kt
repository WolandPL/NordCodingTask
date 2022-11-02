package com.wolandpl.nordcodingtask.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class JsonUtils {

    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        inline fun <reified T : Any> toJson(content: T): String =
            moshi.adapter(T::class.java).indent("    ").toJson(content)
    }
}
