package com.example.bootcamp.di

import com.example.bootcamp.data.remote.api.AuthService
import com.example.bootcamp.data.remote.cookie.PersistentCookieJar
import com.example.bootcamp.data.remote.interceptor.CsrfInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Hilt module providing network-related dependencies. Configures Retrofit, OkHttp, and API
 * services.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.242.47.131:8081"
    private const val TIMEOUT_SECONDS = 30L

    /** Provides logging interceptor for debugging network requests. */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    /** Provides CSRF interceptor for adding X-XSRF-TOKEN header. */
    @Provides
    @Singleton
    fun provideCsrfInterceptor(cookieJar: PersistentCookieJar): CsrfInterceptor {
        return CsrfInterceptor(cookieJar)
    }

    /** Provides configured OkHttpClient with logging, cookies, CSRF protection, and timeouts. */
    @Provides
    @Singleton
    fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            csrfInterceptor: CsrfInterceptor,
            cookieJar: PersistentCookieJar
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(csrfInterceptor)
                .cookieJar(cookieJar)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
    }

    /** Provides Retrofit instance configured with Gson converter. */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    /** Provides AuthService API interface. */
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
}
