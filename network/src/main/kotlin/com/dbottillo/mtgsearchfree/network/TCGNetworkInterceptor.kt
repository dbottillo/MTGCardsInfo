package com.dbottillo.mtgsearchfree.network

import com.dbottillo.mtgsearchfree.util.LOG
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val apiAuthenticatorInterface: ApiAuthenticatorInterface
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 5) {
            LOG.d("we've tried too many times")
            return null
        }
        return if (response.code == 401) {
            val refreshCall = apiAuthenticatorInterface.auth(
                grantType = "client_credentials",
                clientId = BuildConfig.TCG_CLIENT_ID,
                clientSecret = BuildConfig.TCG_CLIENT_SECRET
            ).blockingGet()
            response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshCall.access_token}")
                .build()
        } else {
            null
        }
    }

    private fun responseCount(response: Response?): Int {
        var result = 1
        var currentResponse = response!!.priorResponse
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
}

class TCGTokenInterceptor @Inject constructor(private val tokenRepository: TokenRepository) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${tokenRepository.get()}")
            .build()
        return chain.proceed(newRequest)
    }
}