package com.example.proyecto_informatico.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://31.220.22.166:8080/api/";
    private static final String PREFS_NAME = "mi_prefs";
    private static final String TOKEN_KEY  = "TOKEN_KEY";

    private static Retrofit retrofitInstance;
    private static OkHttpClient okHttpClientInstance;

    public static void init(final Context context) {
        if (okHttpClientInstance == null) {
            // Logging interceptor (opcional)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Interceptor de token (añade Authorization: Bearer <token> si existe)
            Interceptor tokenInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    SharedPreferences prefs =
                            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String rawToken = prefs.getString(TOKEN_KEY, null);

                    Request original = chain.request();
                    if (rawToken == null) {
                        return chain.proceed(original);
                    }

                    String bearer = "Bearer " + rawToken;
                    Request newReq = original.newBuilder()
                            .addHeader("Authorization", bearer)
                            .build();
                    return chain.proceed(newReq);
                }
            };

            okHttpClientInstance = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(tokenInterceptor)
                    .build();
        }

        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClientInstance)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public static ApiService getApiService() {
        if (retrofitInstance == null) {
            throw new IllegalStateException(
                    "Debes llamar a RetrofitClient.init(context) antes de usar getApiService()");
        }
        return retrofitInstance.create(ApiService.class);
    }
}
