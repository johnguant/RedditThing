package com.johnguant.redditthing.redditapi

import android.content.Context

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.johnguant.redditthing.BuildConfig
import com.johnguant.redditthing.redditapi.model.Thing

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {

    private val BASE_URL = "https://oauth.reddit.com/"

    private val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Thing::class.java, ThingDeserializer())

    private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson.create()))


    private val httpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())

    init {
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logging)
        }
    }

    private var addedAuth = false

    fun <S> createService(serviceClass: Class<S>, context: Context): S {
        if (!addedAuth) {
            httpClient.addInterceptor(AuthInterceptor(context))
            addedAuth = true
        }
        httpClient.authenticator(OAuthAuthenticator(context))
        builder.client(httpClient.build())
        val retrofit = builder.build()
        return retrofit.create(serviceClass)
    }
}
