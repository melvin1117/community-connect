package com.su.communityconnect.model.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.su.communityconnect.model.Category
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepository @Inject constructor() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    suspend fun fetchCategories(): List<Category> {
        val categorySnapshot = database.child("categories").get().await()
        val categories = mutableListOf<Category>()
        for (childSnapshot in categorySnapshot.children) {
            val id = childSnapshot.key ?: continue
            val name = childSnapshot.child("name").getValue(String::class.java) ?: continue
            val image = childSnapshot.child("image").getValue(String::class.java) ?: ""
            categories.add(Category(id, name, image))
        }
        return categories
    }
}
