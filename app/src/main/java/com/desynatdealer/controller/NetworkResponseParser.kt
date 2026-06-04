package com.desynatdealer.controller


import org.json.JSONObject
import retrofit2.Response

//Do not remove suspend from here.

suspend fun <T : Any> networkCall(response: Response<T>): ApiResult<T> {
    return if (response.isSuccessful) {
        response.body()?.let { ApiResult.Success(response.body(), response.code()) }
            ?: ApiResult.Error("Empty response body", response.code())
    } else {
        val errorBody = response.errorBody()?.string()
        val messageCode = try {
            if (errorBody != null) {
                val json = JSONObject(errorBody)
                val message = json.optString("message", "Network Error")

//                // Handle "User is blocked by Admin" case explicitly
//                if (message.contains("User is blocked by Admin", ignoreCase = true)) {
//                    UserBlockEvent.isUserBlocked.postValue(true)
//                }

                message
            } else {
                "Network Error"
            }
        } catch (e: Exception) {
            e.message.toString()
        }
        ApiResult.Error(messageCode as String,response.code())
    }
}