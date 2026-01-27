package com.example.bootcamp.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.example.bootcamp.data.remote.api.AuthService
import com.example.bootcamp.data.remote.api.LoanService
import com.example.bootcamp.data.remote.cookie.PersistentCookieJar
import com.example.bootcamp.data.remote.interceptor.CsrfInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideChunkerCollector(
            @ApplicationContext context: Context
    ): com.chuckerteam.chucker.api.ChuckerCollector {
        return ChuckerCollector(
                context = context,
                showNotification = true,
                retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
    }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(
            @ApplicationContext context: Context,
            chuckerCollector: ChuckerCollector
    ): com.chuckerteam.chucker.api.ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
                .collector(chuckerCollector)
                .maxContentLength(250_000L)
                .redactHeaders("Auth-Token", "Bearer", "X-CSRF-TOKEN")
                .alwaysReadResponseBody(true)
                .build()
    }

    private const val TIMEOUT_SECONDS = 30L

    /** Provides logging interceptor for debugging network requests. */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    /** 
     * Provides CSRF interceptor that fetches fresh token before each protected request.
     * Uses Provider<Retrofit> for lazy initialization to avoid circular dependency.
     */
    @Provides
    @Singleton
    fun provideCsrfInterceptor(
        tokenManager: com.example.bootcamp.data.local.TokenManager,
        retrofitProvider: javax.inject.Provider<Retrofit>
    ): CsrfInterceptor {
        return CsrfInterceptor(tokenManager, retrofitProvider)
    }

    /** Provides configured OkHttpClient with logging, cookies, CSRF protection, and timeouts. */
    @Provides
    @Singleton
    fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            csrfInterceptor: CsrfInterceptor,
            cookieJar: PersistentCookieJar,
            chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(chuckerInterceptor)
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
                .baseUrl(com.example.bootcamp.BuildConfig.BASE_URL)
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

    /** Provides LoanService API interface. */
    @Provides
    @Singleton
    fun provideLoanService(retrofit: Retrofit): LoanService {
        return retrofit.create(LoanService::class.java)
    }

    /** Provides UserProfileService API interface. */
    @Provides
    @Singleton
    fun provideUserProfileService(
            retrofit: Retrofit
    ): com.example.bootcamp.data.remote.api.UserProfileService {
        return retrofit.create(com.example.bootcamp.data.remote.api.UserProfileService::class.java)
    }

    /** Provides FCMApiService API interface for push notification management. */
    @Provides
    @Singleton
    fun provideFCMApiService(
            retrofit: Retrofit
    ): com.example.bootcamp.data.remote.api.FCMApiService {
        return retrofit.create(com.example.bootcamp.data.remote.api.FCMApiService::class.java)
    }

    /** Provides UserProductService API interface. */
    @Provides
    @Singleton
    fun provideUserProductService(
        retrofit: Retrofit
    ): com.example.bootcamp.data.remote.api.UserProductService {
        return retrofit.create(com.example.bootcamp.data.remote.api.UserProductService::class.java)
    }
}
