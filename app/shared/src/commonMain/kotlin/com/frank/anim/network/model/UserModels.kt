package com.frank.anim.network.model

import kotlinx.serialization.Serializable

@Serializable
 data class RemoteUser(
     val id: Int,
     val name: String,
     val username: String? = null,
     val email: String? = null
 )
 
 data class User(
     val id: Int,
     val name: String,
     val username: String?,
     val email: String?
 )

