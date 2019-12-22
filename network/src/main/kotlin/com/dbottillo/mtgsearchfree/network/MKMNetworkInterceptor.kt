package com.dbottillo.mtgsearchfree.network

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class MKMNetworkInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val method = "GET"
        val appToken = "pbuqKRF1WTFh7eGU"
        val appSecret = "6DwnH4vn6fdhJEo3rTARQTvvTiQtEFya"
        val nonce = getOAuthNonce()
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        val signatureMethod = "HmacSHA1"
        val version = "1.0"
        val baseUrl = chain.call().request().url.scheme + "://" + chain.call().request().url.host + chain.call().request().url.encodedPath

        val params: MutableList<Pair<String, String>> = mutableListOf(
                "realm" to baseUrl,
                "oauth_consumer_key" to appToken,
                "oauth_token" to "",
                "oauth_nonce" to nonce,
                "oauth_timestamp" to timestamp.toString(),
                "oauth_signature_method" to signatureMethod,
                "oauth_version" to version,
                "search" to "Counterspell" // to generalise
        )

        val baseString = method + "&" + baseUrl.encode() + "&"
        val encodedParams: MutableList<Pair<String, String>> = params.filter { it.first != "realm" }.map {
            Pair(it.first.encode(), it.second.encode())
        }.toMutableList()
        encodedParams.sortBy { it.first }

        val arrayParams: List<String> = encodedParams.map { it.first + "=" + it.second }
        val paramsString = arrayParams.joinToString(separator = "&").encode()
        val combinedString = baseString + paramsString
        val signatureKey = appSecret.encode() + "&"

        val signature = getSignature(baseString = combinedString, signingKey = signatureKey)
        params.add("oauth_signature" to signature)
        params.sortBy { it.first }

        // val authorization = params.map { it.first + "=\"" + it.second + "\"" }.joinToString(",")

        val authorization = "OAuth " +
                "realm=\"" + baseUrl + "\", " +
                "oauth_version=\"" + version + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                "oauth_nonce=\"" + nonce + "\", " +
                "oauth_consumer_key=\"" + appToken + "\", " +
                "oauth_token=, " +
                "oauth_signature_method=\"" + signatureMethod + "\", " +
                "oauth_signature=\"" + signature + "\""

        val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", authorization)
                .build()
        return chain.proceed(newRequest)
    }

    private fun getOAuthNonce(): String {
        return UUID.randomUUID().toString()
    }

    private fun getSignature(baseString: String, signingKey: String): String {
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(signingKey.toByteArray(), mac.algorithm))
        return Base64.encodeToString(mac.doFinal(baseString.toByteArray()), Base64.NO_WRAP).replace("\r\n", "")
    }
}

fun String.encode(): String {
    return URLEncoder.encode(this, "UTF-8")
}