package com.frank.anim.network

import com.frank.anim.network.model.Photo
import com.frank.anim.network.model.RemotePhoto

class ApiClient(
    private val baseUrl: String
) {

    suspend fun getRemotePhotos(
        path: String
    ): Result<List<RemotePhoto>> = getRemotePhotosFromPlatform("$baseUrl$path")

    suspend fun fetchDemoPhotos(limit: Int = 12): List<Photo> {
        val remotePhotos = getRemotePhotos(path = "/photos?_limit=$limit").getOrNull().orEmpty()
        return remotePhotos.map { remote ->
            val normalizedId = if (remote.id > 1000) remote.id % 1000 else remote.id
            val imageUrl = "https://picsum.photos/id/$normalizedId/800/480.jpg"
            val thumbUrl = "https://picsum.photos/id/$normalizedId/320/220.jpg"
            val imageBytes = downloadBytes(imageUrl)
            val thumbBytes = downloadBytes(thumbUrl)
            Photo(
                id = remote.id,
                title = remote.title,
                thumbnailUrl = thumbUrl,
                imageUrl = imageUrl,
                imageData = imageBytes,
                thumbnailData = thumbBytes
            )
        }
    }

    suspend fun downloadBytes(url: String): ByteArray? = downloadBytesFromPlatform(url)
}

fun createDemoApiClient(): ApiClient = ApiClient(
    baseUrl = "https://jsonplaceholder.typicode.com"
)

expect suspend fun getRemotePhotosFromPlatform(url: String): Result<List<RemotePhoto>>

expect suspend fun downloadBytesFromPlatform(url: String): ByteArray?
