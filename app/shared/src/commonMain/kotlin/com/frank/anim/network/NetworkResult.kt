package com.frank.anim.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException

/**
 * 网络请求结果封装
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T, val fromCache: Boolean = false) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

/**
 * 扩展函数：将 Result 转换为 NetworkResult
 */
fun <T> Result<T>.toNetworkResult(): NetworkResult<T> {
    return fold(
        onSuccess = { NetworkResult.Success(it) },
        onFailure = { e ->
            val message = e.message ?: "未知错误"
            val code = when (e) {
                is ClientRequestException -> e.response.status.value
                is ServerResponseException -> e.response.status.value
                is RedirectResponseException -> e.response.status.value
                else -> null
            }
            NetworkResult.Error(message = message, code = code)
        }
    )
}