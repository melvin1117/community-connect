package com.su.communityconnect.model.service

import android.net.Uri
import com.su.communityconnect.model.User

interface UserService {
    suspend fun getUser(uid: String): User?
    suspend fun saveUser(user: User)
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String
    suspend fun updatePreferredCategories(userId: String, categories: List<String>)
    suspend fun updateWishlist(userId: String, wishlist: List<String>)
}
