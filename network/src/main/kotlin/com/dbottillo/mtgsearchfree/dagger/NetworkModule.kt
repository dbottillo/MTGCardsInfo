package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.core.BuildConfig
import com.dbottillo.mtgsearchfree.network.ApiAuthenticatorInterface
import com.dbottillo.mtgsearchfree.network.MKMApiInterface
import com.dbottillo.mtgsearchfree.network.MKMNetworkInterceptor
import com.dbottillo.mtgsearchfree.network.TCGApiInterface
import com.dbottillo.mtgsearchfree.network.TCGTokenInterceptor
import com.dbottillo.mtgsearchfree.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Named("TCG")
    fun provideTCGClient(
        tokenAuthenticator: TokenAuthenticator,
        tokenInterceptor: TCGTokenInterceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .authenticator(tokenAuthenticator)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }

        return okHttpClientBuilder.build()
    }

    @Provides
    @Named("MKM")
    fun provideMKMClient(
        mkmNetworkInterceptor: MKMNetworkInterceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
                .addInterceptor(mkmNetworkInterceptor)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideTCGApiInterface(
        @Named("TCG") okHttpClient: OkHttpClient
    ): TCGApiInterface {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.tcgplayer.com/v1.19.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(TCGApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideMKMApiInterface(
        @Named("MKM") okHttpClient: OkHttpClient
    ): MKMApiInterface {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.cardmarket.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MKMApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticatorService(): ApiAuthenticatorInterface {
        val okHttpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }
        return Retrofit.Builder()
                .client(okHttpClientBuilder.build())
                .baseUrl("https://api.tcgplayer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiAuthenticatorInterface::class.java)
    }
}