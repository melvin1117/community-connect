package com.su.communityconnect.model

import java.io.Serializable

data class User(
    val id: String = "",
    val email: String = "",
    val provider: String = "",
    val displayName: String = "",
    val profilePictureUrl: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val preferredCategories: List<String> = emptyList(),
    val wishlist: List<String> = emptyList(),
) : Serializable
