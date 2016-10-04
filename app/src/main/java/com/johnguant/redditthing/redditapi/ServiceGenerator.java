package com.johnguant.redditthing.redditapi;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.johnguant.redditthing.BuildConfig;
import com.johnguant.redditthing.redditapi.model.Thing;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final String BASE_URL = "https://oauth.reddit.com/";

    private static GsonBuilder gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Thing.class, new ThingDeserializer());

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson.create()));


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .addInterceptor(new HeaderInterceptor());

    static {
        if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }
    }

    private static boolean addedAuth = false;

    public static <S> S createService(Class<S> serviceClass, Context context){
        if(!addedAuth) {
            httpClient.addInterceptor(new AuthInterceptor(context));
            addedAuth = true;
        }
        httpClient.authenticator(new OAuthAuthenticator(context));
        builder.client(httpClient.build());
        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
