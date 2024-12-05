package com.su.communityconnect.model.service.impl

import android.net.Uri
import com.su.communityconnect.model.User
import com.su.communityconnect.model.repository.UserRepository
import com.su.communityconnect.model.service.UserService
import javax.inject.Inject

class UserServiceImpl @Inject constructor(
    private val repository: UserRepository
) : UserService {

    override suspend fun getUser(uid: String): User? = repository.getUser(uid)

    override suspend fun saveUser(user: User) = repository.saveUser(user)

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        return repository.uploadProfileImage(userId, imageUri)
    }

    override suspend fun updatePreferredCategories(userId: String, categories: List<String>) {
        repository.updatePreferredCategories(userId, categories)
    }

    override suspend fun updateWishlist(userId: String, wishlist: List<String>) {
        repository.updateWishlist(userId, wishlist)
    }
}
