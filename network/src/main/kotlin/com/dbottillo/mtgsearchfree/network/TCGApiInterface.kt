package com.dbottillo.mtgsearchfree.network

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TCGApiInterface {
    @GET("/pricing/product/{product_id}")
    fun fetchPrice(@Path("product_id") productId: Int): Single<ApiTCGPrice>
}

interface ApiAuthenticatorInterface {
    @FormUrlEncoded
    @POST("/token")
    fun auth(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Single<TCGAuthentication>
}

class TCGAuthentication(val access_token: String)

class ApiTCGPrice(val success: Boolean, val results: List<ApiTCGPriceResult>)

class ApiTCGPriceResult(
    val productId: Int,
    val lowPrice: Double,
    val highPrice: Double,
    val midPrice: Double,
    val subTypeName: String?
)