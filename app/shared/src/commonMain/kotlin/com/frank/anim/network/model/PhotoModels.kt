package com.frank.anim.network.model

import kotlinx.serialization.Serializable

@Serializable
 data class RemotePhoto(
     val albumId: Int,
     val id: Int,
     val title: String,
     val thumbnailUrl: String,
     val url: String
 )
 
 data class Photo(
     val id: Int,
     val title: String,
     val thumbnailUrl: String?,
     val imageUrl: String?,
     val imageData: ByteArray?,
     val thumbnailData: ByteArray?
 )
