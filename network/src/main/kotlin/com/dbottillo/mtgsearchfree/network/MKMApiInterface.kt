package com.dbottillo.mtgsearchfree.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MKMApiInterface {
    @GET("ws/v2.0/products/find")
    fun fetchProduct(@Query("search") productName: String): Single<ApiMKMProductApi>
}

class ApiMKMProductApi