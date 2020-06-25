package com.cbc.data;


import com.cbc.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Servicegenerator {
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Constants.CHAT_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES) // connect timeout
            .writeTimeout(10, TimeUnit.MINUTES) // write timeout
            .readTimeout(30, TimeUnit.MINUTES);
        // read timeout


    public static <S> S createService(
            Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }

}
