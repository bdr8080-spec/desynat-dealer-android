package com.desynatdealer.controller


enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}

sealed class ApiResult<out T>(val status: ApiStatus, val data: T?, val message: String?, val statusCode: Int?  ) {

    data class Success<out T>(
        private val _data: T?,
        val code: Int? = 200   // default
    ) : ApiResult<T>(
        status = ApiStatus.SUCCESS,
        data = _data,
        message = null,
        statusCode = code
    )

    data class Error(
        private val exception: String,
        val code: Int? = null
    ) : ApiResult<Nothing>(
        status = ApiStatus.ERROR,
        data = null,
        message = exception,
        statusCode = code
    )

    object Loading : ApiResult<Nothing>(
        status = ApiStatus.LOADING,
        data = null,
        message = null,
        statusCode = null
    )
}
