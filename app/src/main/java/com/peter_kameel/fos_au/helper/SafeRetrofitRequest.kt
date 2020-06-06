package com.peter_kameel.fos_au.helper

import com.peter_kameel.fos_au.interfaces.RemoteErrorMassage

abstract class SafeRetrofitRequest {

    companion object {
        suspend fun <T : Any> apiRequest(
            errorListener: RemoteErrorMassage, call: suspend () -> T
        ): T? {
            return try {
                val response = call.invoke()
                response
            } catch (e: Exception) {
                errorListener.onError(e.toString())
                null
            }//end catch
        }//end apiRequest
    }
}