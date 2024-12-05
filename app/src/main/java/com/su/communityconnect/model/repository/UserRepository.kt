package com.su.communityconnect.model.repository

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.su.communityconnect.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) {

    private val usersRef = database.reference.child("users")
    private val storageRef = storage.reference.child("profile_photos")

    suspend fun getUser(uid: String): User? {
        val snapshot = usersRef.child(uid).get().await()
        return snapshot.getValue(User::class.java)
    }

    suspend fun saveUser(user: User) {
        usersRef.child(user.id).setValue(user).await()
    }

    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        val fileRef = storageRef.child(userId).child("profile_pic.jpg")
        val uploadTask = fileRef.putFile(imageUri).await()
        return uploadTask.storage.downloadUrl.await().toString()
    }

    suspend fun updatePreferredCategories(userId: String, categories: List<String>) {
        usersRef.child(userId).child("preferredCategories").setValue(categories).await()
    }

    suspend fun updateWishlist(userId: String, wishlist: List<String>) {
        usersRef.child(userId).child("wishlist").setValue(wishlist).await()
    }
}

