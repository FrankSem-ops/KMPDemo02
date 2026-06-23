package com.frank.anim.network

import com.frank.anim.network.model.RemotePhoto

actual suspend fun getRemotePhotosFromPlatform(url: String): Result<List<RemotePhoto>> =
    Result.failure(UnsupportedOperationException("Wasm demo does not enable remote photo loading yet"))

actual suspend fun downloadBytesFromPlatform(url: String): ByteArray? = null
