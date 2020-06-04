package com.peter_kameel.fos_au.helper

import retrofit2.Response
import java.io.IOException

abstract class SafeRetrofitRequest {
    companion object {
        suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
            val response = call.invoke()
            if (response.isSuccessful) {
                return response.body()!!
            } else {
                throw ApiException(response.code().toString())
            }//end else
        }//end apiRequest fun

        class ApiException(massage: String) : IOException(massage)
    }
}